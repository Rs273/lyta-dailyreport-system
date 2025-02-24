package com.techacademy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.graphics.PdfImageType;
import com.techacademy.constants.ErrorKinds;

public class ImageFileOperator {

    public static final String DIR = "./static/image";
    public static final String DIR_HTML = "/image";
    public static final String CONVERT_DIR = "./static/image/convert";
    public static final String CONVERT_DIR_HTML = "/image/convert";
    public static final String TMP_DIR = "./static/image/tmp";

    // 画像ファイルをセーブする
    public static ErrorKinds save(Integer reportId, MultipartFile file) {

        // 画像ファイルのチェックを行う
        ErrorKinds result = imageFileCheck(file);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // ファイルを保存するためのディレクトリを作成する
        String dirPath = DIR + File.separator + reportId.toString();
        if(Files.notExists(new File(dirPath).toPath())){
            try {
                Files.createDirectory(new File(dirPath).toPath());
            } catch(IOException ex) {
                throw new RuntimeException("Error make a directory.", ex);
            }
        }

        // ファイルを保存する
        String filePath = dirPath + File.separator + file.getOriginalFilename();
        Path destination = new File(filePath).toPath();
        try{
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file.", e);
        }

        // PDFファイルならば
        if(file.getContentType().equals("application/pdf")) {
            PdfDocument pdf = new PdfDocument();
            pdf.loadFromFile(dirPath + File.separator + file.getOriginalFilename());

            // ページ数が2ページ以上だった場合
            if(pdf.getPages().getCount() > 1) {
                delete(filePath);
                return ErrorKinds.PDFPAGECHECK_ERROR;
            }

            // jpgファイルを保存するためのディレクトリを作成する
            String covertDirPath = CONVERT_DIR + File.separator + reportId.toString();
            if(Files.notExists(new File(covertDirPath).toPath())){
                try {
                    Files.createDirectory(new File(covertDirPath).toPath());
                } catch(IOException ex) {
                    throw new RuntimeException("Error make a directory.", ex);
                }
            }

            BufferedImage image = pdf.saveAsImage(0, PdfImageType.Bitmap, 300, 300);

            BufferedImage newImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            newImg.getGraphics().drawImage(image, 0, 0, null);

            // 画像データをjpgファイルとして作成する
            String basename = file.getOriginalFilename();
            String filename = basename.substring(0,basename.lastIndexOf('.'));
            File covertFile = new File(covertDirPath + File.separator + filename + ".jpg");
            try {
                ImageIO.write(newImg, "JPEG", covertFile);
            } catch (IOException e) {
                throw new RuntimeException("Error covert to jpg file.", e);
            }
            pdf.close();
        }

        return ErrorKinds.SUCCESS;
    }

    // 画像ファイル削除（PDFの場合は変換したファイルむ含めて削除）
    public static ErrorKinds deleteWithCovertedFile(Integer reportId, String filename) {

        delete(DIR + File.separator + reportId.toString() + File.separator + filename);

        // pdfの場合、表示用に作成したjpgファイルも削除する
        String extension = filename.substring(filename.lastIndexOf('.'));
        if(extension.equals(".pdf")) {
            delete(CONVERT_DIR + File.separator + reportId.toString() + File.separator + filename.substring(0,filename.lastIndexOf('.')) + ".jpg");
        }
        return ErrorKinds.SUCCESS;
    }

    private static ErrorKinds delete(String filePath) {
        File file = new File(filePath);

        if(file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch(IOException e) {
                throw new RuntimeException("Error delete file.", e);
            }
        }
        return ErrorKinds.CHECK_OK;
    }

    // Exifの撮影日時情報を返す
    private static Date getDateFromExif(String filePath) {
        File file = new File(filePath);
        try{
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory subIfdDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            Date date = subIfdDirectory.getDateOriginal();
            return date;
        } catch(IOException e) {
            throw new RuntimeException("Error get exif.", e);
        } catch(ImageProcessingException e) {
            throw new RuntimeException("An exception class thrown upon unexpected and fatal conditions while processing a image file.", e);
        }

    }

    // 画像ファイルチェック
    private static ErrorKinds imageFileCheck(MultipartFile imageFile) {

        // ファイル名の文字数チェック処理
        if(isOutOfRangeFileName(imageFile.getOriginalFilename())) {
            return ErrorKinds.FILENAME_RANGECHECK_ERROR;
        }

        // ファイルサイズの上限チェック処理
        if(isOutOfRangeFileSize(imageFile)) {
            return ErrorKinds.FILESIZECHECK_ERROR;
        }

        // 画像ファイルチェック処理
        if(isImageFile(imageFile)) {
            return ErrorKinds.IMAGEFILECHECK_ERROR;
        }

        return ErrorKinds.CHECK_OK;
    }

    // ファイル名の文字数チェック処理
    private static boolean isOutOfRangeFileName(String filename) {
        int length = filename.length();
        return 100 < length;
    }

    // ファイルサイズの上限チェック処理
    private static boolean isOutOfRangeFileSize(MultipartFile file) {
        long size = file.getSize();
        return size > 5 * 1024 * 1024; // 5MB
    }

    // 画像ファイルチェック処理
    private static boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if(contentType.startsWith("image/")) {
            return false;
        } else if(contentType.equals("application/pdf")) {
            return false;
        }

        return true;
    }

}
