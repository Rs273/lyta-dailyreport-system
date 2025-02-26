package com.techacademy;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Comment;
import com.techacademy.entity.Reaction;
import com.techacademy.entity.Report;

import jakarta.servlet.http.HttpServletResponse;

@Component
@PropertySource("classpath:csv.properties")
public class FileDownloader {

    private static final String TMP_DIR = "./static/tmp";

    @Value("${header.flg}")
    private boolean headerFlg;

    @Value("${header.report}")
    private String reportHeader;

    @Value("${header.comment}")
    private String commentHeader;

    @Value("${format.reportDate}")
    private String reportDateFormat;

    @Value("${format.createdAt}")
    private String createdAtFormat;

    @Value("${format.updatedAt}")
    private String updatedAtFormat;

    @Value("${noData}")
    private String noData;

    public ErrorKinds download(Report report, List<Reaction> reactionList, List<Comment> commentList, HttpServletResponse response) {

        // csvファイルのパスを決定
        String fileName = report.getReportDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + report.getEmployee().getName() + ".csv";
        String filePath = TMP_DIR + File.separator + fileName;

        // csvファイルの内容を追記
        StringBuilder headers = new StringBuilder("");
        StringBuilder datas = new StringBuilder("");

        // 日報情報追記
        addReportInfo(headers, datas, report);
        // リアクション情報追記
        addReactionInfo(headers, datas, reactionList);
        // コメント情報追記
        if(commentList != null) {
            addCommentInfo(headers, datas, commentList);
        }

        // csvファイル作成
        try{
            FileWriter fw = new FileWriter(filePath, false);

            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            if(headerFlg) {
                pw.print(headers.toString());
                pw.println();
            }
            pw.print(datas.toString());
            pw.println();

            pw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException("Error writing csv file.", e);
        }

        if(report.getImageFileName() == null) {
            // 画像ファイルがない場合、csvファイルをダウンロードさせる
            downloadFile(response, Path.of(filePath), fileName);
        } else {
            // 画像ファイルがある場合、zipファイルをダウンロードさせる

            // zipファイルのファイル名とパス名を設定
            String zipFileName = report.getReportDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + report.getEmployee().getName() + ".zip";
            String zipFilePath = TMP_DIR + File.separator + zipFileName;

            // zipファイルを作成する
            List<String> contentPaths = new ArrayList();
            contentPaths.add(filePath); // csvファイル
            contentPaths.add(ImageFileOperator.DIR + File.separator + report.getId().toString() + File.separator + report.getImageFileName());
            createZipFile(Path.of(zipFilePath), contentPaths);

            // zipファイルをダウンロードさせる
            downloadFile(response, Path.of(zipFilePath), zipFileName);

            // zipファイルを削除
            deleteFile(zipFilePath);
        }

        // ファイルを削除する
        deleteFile(filePath);

        return ErrorKinds.SUCCESS;
    }


    private ErrorKinds deleteFile(String filePath) {
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

    private ErrorKinds addReportInfo(StringBuilder headers, StringBuilder datas, Report report) {

        // headersに項目名を追記する
        headers.append(reportHeader);

        // datasに日報情報を追記する
        datas.append(report.getReportDate().format(DateTimeFormatter.ofPattern(reportDateFormat)) + ",");
        datas.append(report.getEmployee().getName() + ",");
        datas.append(report.getTitle() + ",");
        datas.append(report.getContent() + ",");
        if(report.getImageFileName() == null) {
            datas.append(noData + ",");
        } else {
            datas.append(report.getImageFileName() + ",");
        }
        datas.append(report.getCreatedAt().format(DateTimeFormatter.ofPattern(createdAtFormat)) + ",");
        datas.append(report.getUpdatedAt().format(DateTimeFormatter.ofPattern(updatedAtFormat)) + ",");

        return ErrorKinds.SUCCESS;
    }

    private ErrorKinds addReactionInfo(StringBuilder headers, StringBuilder datas, List<Reaction> reactionList) {

        for(Reaction reaction: reactionList) {
            // headersに絵文字を追記する
            headers.append(reaction.getEmoji() + ",");
            // datasにカウントを追記する
            datas.append(reaction.getCount().toString() + ",");
        }

        return ErrorKinds.SUCCESS;
    }

    private ErrorKinds addCommentInfo(StringBuilder headers, StringBuilder datas, List<Comment> commentList) {

        for(Comment comment: commentList) {
            // headersに項目名を追記する
            headers.append(commentHeader);
            // datasにカウントを追記する
            datas.append(comment.getEmployee().getName() + ",");
            datas.append(comment.getContent() + ",");
            datas.append(comment.getCreatedAt().format(DateTimeFormatter.ofPattern(createdAtFormat)) + ",");
            datas.append(comment.getUpdatedAt().format(DateTimeFormatter.ofPattern(updatedAtFormat)) + ",");
        }

        return ErrorKinds.SUCCESS;
    }

    //
    private ErrorKinds downloadFile(HttpServletResponse response, Path filePath, String fileName) {

        String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=UTF-8''%s";
        String outputFileName = String.format(CONTENT_DISPOSITION_FORMAT, URLEncoder.encode(fileName, StandardCharsets.UTF_8),
            UriUtils.encode(fileName, StandardCharsets.UTF_8.name()));

        Resource resource = new PathResource(filePath);

        // ダウンロードさせる
        try (BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());) {
            response.setContentType(getContentType(filePath));
            response.setContentLengthLong(resource.contentLength());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, outputFileName);
            out.write(Files.readAllBytes(filePath));
            out.flush();
        }catch (IOException e) {
            throw new RuntimeException("Error downloading csv file.", e);
        }

        return ErrorKinds.SUCCESS;
    }

    private String getContentType(Path path) {
        String contenType = null;
        try {
            contenType = Files.probeContentType(path);
        }catch (IOException e) {
            throw new RuntimeException("Error get contentType.", e);
        }
        return contenType;
    }

    // zipファイルを作成する
    private ErrorKinds createZipFile(Path zipFilePath, List<String> contentPaths) {

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath.toFile())));

            for(String path: contentPaths) {
                zipOutputStream.putNextEntry(new ZipEntry(new File(path).getName()));
                zipOutputStream.write(Files.readAllBytes(Path.of(path)));
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error creating zip file.", e);
        }

        return ErrorKinds.SUCCESS;
    }
}
