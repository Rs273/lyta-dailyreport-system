package com.techacademy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Comment;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Reaction;
import com.techacademy.entity.Report;

import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class FileDownloaderTest {

    @Autowired
    private ImageFileOperator imageFileOperator;

    @Autowired
    private FileDownloader fileDownloader;

    // テストケース 画像ファイルありコメントあり
    @Test
    void testDownloadSuccess1() {
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");
        employeeCode1.setName("煌木　太郎");

        Report reportId1 = new Report();
        reportId1.setId(1);
        reportId1.setReportDate(LocalDate.now());
        reportId1.setTitle("煌木　太郎の記載、タイトル");
        reportId1.setContent("煌木　太郎の記載、内容");
        reportId1.setEmployee(employeeCode1);
        reportId1.setImageFileName("dog.jpeg");
        reportId1.setImageFilePath("/image/1/dog.jpeg");
        reportId1.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        reportId1.setCreatedAt(now);
        reportId1.setUpdatedAt(now);

        Reaction reactionId1 = new Reaction();
        reactionId1.setId(1);
        reactionId1.setEmoji("👍");
        reactionId1.setCount(0);
        reactionId1.setReport(reportId1);
        Reaction reactionId2 = new Reaction();
        reactionId2.setId(2);
        reactionId2.setEmoji("✅");
        reactionId2.setCount(0);
        reactionId2.setReport(reportId1);
        Reaction reactionId3 = new Reaction();
        reactionId3.setId(3);
        reactionId3.setEmoji("💪");
        reactionId3.setCount(0);
        reactionId3.setReport(reportId1);
        Reaction reactionId4 = new Reaction();
        reactionId4.setId(4);
        reactionId4.setEmoji("👀");
        reactionId4.setCount(0);
        reactionId4.setReport(reportId1);
        Reaction reactionId5 = new Reaction();
        reactionId5.setId(5);
        reactionId5.setEmoji("🙌");
        reactionId5.setCount(0);
        reactionId5.setReport(reportId1);

        List<Reaction> reactionList = new ArrayList<Reaction>();
        reactionList.add(reactionId1);
        reactionList.add(reactionId2);
        reactionList.add(reactionId3);
        reactionList.add(reactionId4);
        reactionList.add(reactionId5);

        Comment commentId1 = new Comment();
        commentId1.setId(1);
        commentId1.setContent("煌木　太郎のコメント、内容。");
        commentId1.setEmployee(employeeCode1);
        commentId1.setReport(reportId1);
        commentId1.setEditingFlg(false);
        commentId1.setDeleteFlg(false);
        commentId1.setCreatedAt(now);
        commentId1.setUpdatedAt(now);

        List<Comment> commentList = new ArrayList<Comment>();
        commentList.add(commentId1);

        MockHttpServletResponse response = new MockHttpServletResponse();

        ErrorKinds result = fileDownloader.download(reportId1, reactionList, commentList, response);
        assertEquals(result, ErrorKinds.SUCCESS);
    }


    // テストケース 画像ファイルなしコメントなし
    @Test
    void testDownloadSuccess2() {
        Employee employeeCode2 = new Employee();
        employeeCode2.setCode("2");
        employeeCode2.setName("田中　太郎");
        
        Report reportId2 = new Report();
        reportId2.setId(2);
        reportId2.setReportDate(LocalDate.now());
        reportId2.setTitle("田中　太郎の記載、タイトル");
        reportId2.setContent("田中　太郎の記載、内容");
        reportId2.setEmployee(employeeCode2);
        reportId2.setImageFileName(null);
        reportId2.setImageFilePath(null);
        reportId2.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        reportId2.setCreatedAt(now);
        reportId2.setUpdatedAt(now);

        Reaction reactionId6 = new Reaction();
        reactionId6.setId(6);
        reactionId6.setEmoji("👍");
        reactionId6.setCount(0);
        reactionId6.setReport(reportId2);
        Reaction reactionId7 = new Reaction();
        reactionId7.setId(7);
        reactionId7.setEmoji("✅");
        reactionId7.setCount(0);
        reactionId7.setReport(reportId2);
        Reaction reactionId8 = new Reaction();
        reactionId8.setId(8);
        reactionId8.setEmoji("💪");
        reactionId8.setCount(0);
        reactionId8.setReport(reportId2);
        Reaction reactionId9 = new Reaction();
        reactionId9.setId(9);
        reactionId9.setEmoji("👀");
        reactionId9.setCount(0);
        reactionId9.setReport(reportId2);
        Reaction reactionId10 = new Reaction();
        reactionId10.setId(10);
        reactionId10.setEmoji("🙌");
        reactionId10.setCount(0);
        reactionId10.setReport(reportId2);

        List<Reaction> reactionList = new ArrayList<Reaction>();
        reactionList.add(reactionId6);
        reactionList.add(reactionId7);
        reactionList.add(reactionId8);
        reactionList.add(reactionId9);
        reactionList.add(reactionId10);

        MockHttpServletResponse response = new MockHttpServletResponse();

        ErrorKinds result = fileDownloader.download(reportId2, reactionList, null, response);
        assertEquals(result, ErrorKinds.SUCCESS);
    }

}
