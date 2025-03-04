package com.techacademy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.ImageFileOperator;
import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReactionService reactionService;

    @Mock
    private ImageFileOperator imageFileOperator;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    private void beforeEach() {
        // 戻り値を作成
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");
        Report reportId1 = new Report();
        reportId1.setId(1);
        LocalDate date = LocalDate.parse("20250226", DateTimeFormatter.ofPattern("yyyyMMdd"));
        reportId1.setReportDate(date);
        reportId1.setTitle("煌木　太郎の記載、タイトル");
        reportId1.setContent("煌木　太郎の記載、内容");
        reportId1.setEmployee(employeeCode1);
        reportId1.setImageFileName("dog.jpeg");
        reportId1.setImageFilePath("/image/1/dog.jpeg");
        reportId1.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        reportId1.setCreatedAt(now);
        reportId1.setUpdatedAt(now);


        Employee employeeCode2 = new Employee();
        employeeCode2.setCode("2");
        Report reportId2 = new Report();
        reportId2.setId(2);
        reportId2.setReportDate(date);
        reportId2.setTitle("田中　太郎の記載、タイトル");
        reportId2.setContent("田中　太郎の記載、内容");
        reportId2.setEmployee(employeeCode2);
        reportId2.setImageFileName(null);
        reportId2.setImageFilePath(null);
        reportId2.setDeleteFlg(false);
        reportId2.setCreatedAt(now);
        reportId2.setUpdatedAt(now);


        Report reportId3 = new Report();
        reportId3.setId(3);
        LocalDate date2 = LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd"));
        reportId3.setReportDate(date2);
        reportId3.setTitle("田中　太郎の記載、タイトル2");
        reportId3.setContent("田中　太郎の記載、内容2");
        reportId3.setEmployee(employeeCode2);
        reportId3.setImageFileName(null);
        reportId3.setImageFilePath(null);
        reportId3.setDeleteFlg(false);
        reportId3.setCreatedAt(now);
        reportId3.setUpdatedAt(now);

        List<Report> reportList = new ArrayList<Report>();
        reportList.add(reportId1);
        reportList.add(reportId2);
        reportList.add(reportId3);

     // スタブを設定
        Mockito.when(reportRepository.findById(1)).thenReturn(Optional.of(reportId1));
        Mockito.when(reportRepository.findById(2)).thenReturn(Optional.of(reportId2));
        Mockito.when(reportRepository.findById(3)).thenReturn(Optional.of(reportId3));
        Mockito.when(reportRepository.findAll()).thenReturn(reportList);
    }

    @Test
    void testSave() {
        // reportRepositoryのsaveメソッドが問題なく終了するようスタブを設定
        Mockito.when(reportRepository.save(any())).thenReturn(null);
        // reactionServiceのsaveAllメソッドが問題なく終了するようスタブを設定
        Mockito.when(reactionService.saveAll(any())).thenReturn(ErrorKinds.SUCCESS);

        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");

        // 日付が重複し、保存が失敗する場合
        Report report = new Report();
        LocalDate date = LocalDate.parse("20250226", DateTimeFormatter.ofPattern("yyyyMMdd"));
        report.setReportDate(date);
        report.setTitle("煌木　太郎の記載、タイトル2");
        report.setContent("煌木　太郎の記載、内容2");
        report.setEmployee(employeeCode1);
        report.setImageFileName("dog.jpeg");
        report.setImageFilePath("/image/1/dog.jpeg");

        ErrorKinds result = reportService.save(report);
        assertEquals(result, ErrorKinds.DATECHECK_ERROR);

        // 保存が成功する場合
        LocalDate date2 = LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd"));
        report.setReportDate(date2);

        ErrorKinds result2 = reportService.save(report);
        assertEquals(result2, ErrorKinds.SUCCESS);
        // reportRepositoryのsaveメソッドが1回呼ばれたことを確認する
        Mockito.verify(reportRepository, times(1)).save(any());
    }

    @Test
    void testUpdate() {
        // reportRepositoryのsaveメソッドが問題なく終了するようスタブを設定
        Mockito.when(reportRepository.save(any())).thenReturn(null);

        // 日付が重複し、保存が失敗する場合
        Employee employeeCode2 = new Employee();
        employeeCode2.setCode("2");
        Report reportId3 = new Report();
        reportId3.setId(3);
        LocalDate date = LocalDate.parse("20250226", DateTimeFormatter.ofPattern("yyyyMMdd"));
        reportId3.setReportDate(date);
        reportId3.setTitle("田中　太郎の記載、タイトル2");
        reportId3.setContent("田中　太郎の記載、内容2");
        reportId3.setEmployee(employeeCode2);
        reportId3.setImageFileName(null);
        reportId3.setImageFilePath(null);

        ErrorKinds result = reportService.update(reportId3);
        assertEquals(result, ErrorKinds.DATECHECK_ERROR);

        // 保存が成功する場合
        LocalDate date2 = LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd"));
        reportId3.setReportDate(date2);
        reportId3.setTitle("田中　太郎のタイトル2");

        ErrorKinds result2 = reportService.update(reportId3);
        assertEquals(result2, ErrorKinds.SUCCESS);
        // reportRepositoryのsaveメソッドが1回呼ばれたことを確認する
        Mockito.verify(reportRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        // reactionServiceのdeleteAllメソッドが問題なく終了するようスタブを設定
        Mockito.when(reactionService.deleteAll(any())).thenReturn(ErrorKinds.SUCCESS);
        // imageFileOperatorのdeleteWithCovertedFileメソッドが問題なく終了するようスタブを設定
        Mockito.when(imageFileOperator.deleteWithCovertedFile(any(), any())).thenReturn(ErrorKinds.SUCCESS);

        // 画像ファイルを含む日報の場合
        ErrorKinds result = reportService.delete(1);
        assertEquals(result, ErrorKinds.SUCCESS);

        //　画像ファイルを含まない日報の場合
        ErrorKinds result2 = reportService.delete(2);
        assertEquals(result2, ErrorKinds.SUCCESS);

    }

    @Test
    void testFindAll() {
        List<Report> reports = reportService.findAll();

        LocalDate date = LocalDate.parse("20250226", DateTimeFormatter.ofPattern("yyyyMMdd"));

        Report reportId1 = reports.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertTrue(reportId1.getReportDate().equals(date));
        assertEquals(reportId1.getTitle(), "煌木　太郎の記載、タイトル");
        assertEquals(reportId1.getContent(),"煌木　太郎の記載、内容");
        assertEquals(reportId1.getEmployee().getCode(), "1");
        assertEquals(reportId1.getImageFileName(), "dog.jpeg");
        assertEquals(reportId1.getImageFilePath(), "/image/1/dog.jpeg");
        assertFalse(reportId1.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        Report reportId2 = reports.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertTrue(reportId2.getReportDate().equals(date));
        assertEquals(reportId2.getTitle(), "田中　太郎の記載、タイトル");
        assertEquals(reportId2.getContent(),"田中　太郎の記載、内容");
        assertEquals(reportId2.getEmployee().getCode(), "2");
        assertEquals(reportId2.getImageFileName(), null);
        assertEquals(reportId2.getImageFilePath(), null);
        assertFalse(reportId2.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可
    }

    @Test
    void testFindById() {
        Report report = reportService.findById(1);
        LocalDate date = LocalDate.parse("20250226", DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertTrue(report.getReportDate().equals(date));
        assertEquals(report.getTitle(), "煌木　太郎の記載、タイトル");
        assertEquals(report.getContent(),"煌木　太郎の記載、内容");
        assertEquals(report.getEmployee().getCode(), "1");
        assertEquals(report.getImageFileName(), "dog.jpeg");
        assertEquals(report.getImageFilePath(), "/image/1/dog.jpeg");
        assertFalse(report.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        Report reportNull = reportService.findById(100);
        assertEquals(reportNull, null);
    }

    @Test
    void testFindByEmployee() {
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");

        List<Report> reports = reportService.findByEmployee(employeeCode1);

        LocalDate date = LocalDate.parse("20250226", DateTimeFormatter.ofPattern("yyyyMMdd"));

        Report reportId1 = reports.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertTrue(reportId1.getReportDate().equals(date));
        assertEquals(reportId1.getTitle(), "煌木　太郎の記載、タイトル");
        assertEquals(reportId1.getContent(),"煌木　太郎の記載、内容");
        assertEquals(reportId1.getEmployee().getCode(), "1");
        assertEquals(reportId1.getImageFileName(), "dog.jpeg");
        assertEquals(reportId1.getImageFilePath(), "/image/1/dog.jpeg");
        assertFalse(reportId1.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可
    }

    @Test
    void testGetMaxOfId() {
        int maxOfId = reportService.getMaxOfId();

        assertEquals(maxOfId, 3);
    }

}
