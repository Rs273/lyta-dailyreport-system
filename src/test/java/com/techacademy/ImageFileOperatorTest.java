package com.techacademy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.constants.ErrorKinds;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ImageFileOperatorTest {

    @Autowired
    private ImageFileOperator imageFileOperator;

    // テストケース1 JPG正常終了
    @Test
    void testSaveSuccess1() throws Exception{
        byte[] fileImage = null;
        File upfile = new File("./static/test/image/dog2.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile file = new MockMultipartFile("imageFile", "dog2.jpeg", "image/jpeg", fileImage);

        ErrorKinds result = imageFileOperator.save(6, file);
        assertEquals(result, ErrorKinds.SUCCESS);
    }

    // テストケース2 PDF正常終了
    @Test
    void testSaveSuccess2() throws Exception{
        byte[] fileImage = null;
        File upfile = new File("./static/test/image/画面遷移図.pdf");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile file = new MockMultipartFile("imageFile", "画面遷移図.pdf", "application/pdf", fileImage);

        ErrorKinds result = imageFileOperator.save(7, file);
        assertEquals(result, ErrorKinds.SUCCESS);
    }

    // テストケース3 ファイル名101文字以上
    @Test
    void testSaveError1() throws Exception {
        byte[] fileImage = null;
        File upfile = new File("./static/test/image/あああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile file = new MockMultipartFile("imageFile", "あ".repeat(100) + ".jpg", "image/jpeg", fileImage);

        ErrorKinds result = imageFileOperator.save(10, file);
        assertEquals(result, ErrorKinds.FILENAME_RANGECHECK_ERROR);
    }

    // テストケース4 画像ファイル以外が選択
    @Test
    void testSaveError2() throws Exception {
        byte[] fileImage = null;
        File upfile = new File("./static/test/image/あいうえお.txt");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile file = new MockMultipartFile("imageFile", "あいうえお.txt", "text/plain", fileImage);

        ErrorKinds result = imageFileOperator.save(10, file);
        assertEquals(result, ErrorKinds.IMAGEFILECHECK_ERROR);
    }

    // テストケース5 ファイルサイズが5MBより大きい
    @Test
    void testSaveError3() throws Exception {
        byte[] fileImage = null;
        File upfile = new File("./static/test/image/9MB");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile file = new MockMultipartFile("imageFile", "9MB", "application/octet-stream", fileImage);

        ErrorKinds result = imageFileOperator.save(10, file);
        assertEquals(result, ErrorKinds.FILESIZECHECK_ERROR);
    }

    // テストケース6 2ページ以上のPDF
    @Test
    void testSaveError4() throws Exception{
        byte[] fileImage = null;
        File upfile = new File("./static/test/image/設計書.pdf");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile file = new MockMultipartFile("imageFile", "設計書.pdf", "application/pdf", fileImage);

        ErrorKinds result = imageFileOperator.save(10, file);
        assertEquals(result, ErrorKinds.PDFPAGECHECK_ERROR);
    }


    // テストケース1 SVG正常終了
    @Test
    void testDeleteWithCovertedFileSuccess1() {
        ErrorKinds result = imageFileOperator.deleteWithCovertedFile(8, "mouse.svg");
        assertEquals(result, ErrorKinds.SUCCESS);
    }

    // テストケース2 PDF正常終了
    @Test
    void testDeleteWithCovertedFileSuccess2() {
        ErrorKinds result = imageFileOperator.deleteWithCovertedFile(9, "画面遷移図.pdf");
        assertEquals(result, ErrorKinds.SUCCESS);
    }

}
