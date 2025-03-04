package com.techacademy.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.techacademy.ImageFileOperator;
import com.techacademy.entity.Comment;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Reaction;
import com.techacademy.service.UserDetail;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;

    private final WebApplicationContext webApplicationContext;

    ReportControllerTest(WebApplicationContext context) {
        this.webApplicationContext = context;
    }

    @BeforeEach
    void beforeEach() {
        // Spring Securityを有効にする
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    // 日報一覧画面
    // テストケース1 管理者がログイン
    @Test
    void testList1() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        // HTTPリクエストに対するレスポンスの検証
        MvcResult result = mockMvc.perform((get("/reports")).with(user(userDetail)).with(csrf())) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("reportList")) // Modelの内容を確認
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("reports/list")) // viewの確認
                .andReturn(); // 内容の取得

        @SuppressWarnings("unchecked")
        List<Report> reportList = (List<Report>) result.getModelAndView().getModel().get("reportList");

        assertEquals(reportList.size(), 3);

        LocalDate currentDate = LocalDate.now();

        Report reportId1 = reportList.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertTrue(reportId1.getReportDate().equals(currentDate));
        assertEquals(reportId1.getTitle(), "煌木　太郎の記載、タイトル");
        assertEquals(reportId1.getContent(),"煌木　太郎の記載、内容");
        assertEquals(reportId1.getEmployee().getCode(), "1");
        assertEquals(reportId1.getImageFileName(), "dog.jpeg");
        assertEquals(reportId1.getImageFilePath(), "/image/1/dog.jpeg");
        assertFalse(reportId1.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        Report reportId2 = reportList.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertTrue(reportId2.getReportDate().equals(currentDate));
        assertEquals(reportId2.getTitle(), "田中　太郎の記載、タイトル");
        assertEquals(reportId2.getContent(),"田中　太郎の記載、内容");
        assertEquals(reportId2.getEmployee().getCode(), "2");
        assertEquals(reportId2.getImageFileName(), null);
        assertEquals(reportId2.getImageFilePath(), null);
        assertFalse(reportId2.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        Report reportId3 = reportList.stream().filter(e -> e.getId().equals(3)).findFirst().get();
        assertTrue(reportId3.getReportDate().equals(currentDate.plusDays(1)));
        assertEquals(reportId3.getTitle(), "田中　太郎の記載、タイトル");
        assertEquals(reportId3.getContent(),"田中　太郎の記載、内容");
        assertEquals(reportId3.getEmployee().getCode(), "2");
        assertEquals(reportId3.getImageFileName(), null);
        assertEquals(reportId3.getImageFilePath(), null);
        assertFalse(reportId3.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可
    }

    // テストケース2 一般従業員がログイン
    @Test
    void testList2() throws Exception {

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        // HTTPリクエストに対するレスポンスの検証
        MvcResult result = mockMvc.perform((get("/reports")).with(user(userDetail)).with(csrf())) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("reportList")) // Modelの内容を確認
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("reports/list")) // viewの確認
                .andReturn(); // 内容の取得

        @SuppressWarnings("unchecked")
        List<Report> reportList = (List<Report>) result.getModelAndView().getModel().get("reportList");

        assertEquals(reportList.size(), 2);

        LocalDate currentDate = LocalDate.now();

        Report reportId2 = reportList.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertTrue(reportId2.getReportDate().equals(currentDate));
        assertEquals(reportId2.getTitle(), "田中　太郎の記載、タイトル");
        assertEquals(reportId2.getContent(),"田中　太郎の記載、内容");
        assertEquals(reportId2.getEmployee().getCode(), "2");
        assertEquals(reportId2.getImageFileName(), null);
        assertEquals(reportId2.getImageFilePath(), null);
        assertFalse(reportId2.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        Report reportId3 = reportList.stream().filter(e -> e.getId().equals(3)).findFirst().get();
        assertTrue(reportId3.getReportDate().equals(currentDate.plusDays(1)));
        assertEquals(reportId3.getTitle(), "田中　太郎の記載、タイトル");
        assertEquals(reportId3.getContent(),"田中　太郎の記載、内容");
        assertEquals(reportId3.getEmployee().getCode(), "2");
        assertEquals(reportId3.getImageFileName(), null);
        assertEquals(reportId3.getImageFilePath(), null);
        assertFalse(reportId3.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可
    }

    // 日報詳細画面
    @Test
    void testDetail() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        // HTTPリクエストに対するレスポンスの検証
        MvcResult result = mockMvc.perform((get("/reports/1/")).with(user(userDetail)).with(csrf())) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("report")) // Modelの内容を確認
                .andExpect(model().attributeExists("reactionList"))
                .andExpect(model().attributeExists("commentList"))
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("reports/detail")) // viewの確認
                .andReturn(); // 内容の取得

        Report report = (Report) result.getModelAndView().getModel().get("report");
        assertTrue(report.getReportDate().equals(LocalDate.now()));
        assertEquals(report.getTitle(), "煌木　太郎の記載、タイトル");
        assertEquals(report.getContent(),"煌木　太郎の記載、内容");
        assertEquals(report.getEmployee().getCode(), "1");
        assertEquals(report.getImageFileName(), "dog.jpeg");
        assertEquals(report.getImageFilePath(), "/image/1/dog.jpeg");
        assertFalse(report.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        @SuppressWarnings("unchecked")
        List<Reaction> reactionList = (List<Reaction>) result.getModelAndView().getModel().get("reactionList");
        assertEquals(reactionList.size(), 5);

        Reaction reactionId1 = reactionList.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(reactionId1.getId(), 1);
        assertEquals(reactionId1.getCount(), 0);
        assertEquals(reactionId1.getEmoji(), "👍");
        assertEquals(reactionId1.getReport().getId(), 1);

        Reaction reactionId2 = reactionList.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertEquals(reactionId2.getId(), 2);
        assertEquals(reactionId2.getCount(), 0);
        assertEquals(reactionId2.getEmoji(), "✅");
        assertEquals(reactionId2.getReport().getId(), 1);

        Reaction reactionId3 = reactionList.stream().filter(e -> e.getId().equals(3)).findFirst().get();
        assertEquals(reactionId3.getId(), 3);
        assertEquals(reactionId3.getCount(), 0);
        assertEquals(reactionId3.getEmoji(), "💪");
        assertEquals(reactionId3.getReport().getId(), 1);

        Reaction reactionId4 = reactionList.stream().filter(e -> e.getId().equals(4)).findFirst().get();
        assertEquals(reactionId4.getId(), 4);
        assertEquals(reactionId4.getCount(), 0);
        assertEquals(reactionId4.getEmoji(), "👀");
        assertEquals(reactionId4.getReport().getId(), 1);

        Reaction reactionId5 = reactionList.stream().filter(e -> e.getId().equals(5)).findFirst().get();
        assertEquals(reactionId5.getId(), 5);
        assertEquals(reactionId5.getCount(), 0);
        assertEquals(reactionId5.getEmoji(), "🙌");
        assertEquals(reactionId5.getReport().getId(), 1);

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) result.getModelAndView().getModel().get("commentList");
        assertEquals(commentList.size(), 0);
    }

    // 日報新規登録画面
    @Test
    void testCreate() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform((get("/reports/add")).with(user(userDetail)).with(csrf())) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("report")) // Modelの内容を確認
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("reports/new")); // viewの確認
    }

    // 日報新規登録処理
    // テストケース1 画像ファイルなし正常終了
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddSuccess1() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(redirectedUrl("/reports"));
    }

    // テストケース2 画像ファイル(JPEG)あり正常終了
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddSuccess2() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/dog2.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "dog2.jpg", "image/jpeg", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(redirectedUrl("/reports"));
    }

    // テストケース3 画像ファイル(PDF)あり正常終了
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddSuccess3() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/画面遷移図.pdf");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "画面遷移図.pdf", "application/pdf", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(redirectedUrl("/reports"));
    }

    // テストケース4 日付入力漏れ
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError1() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(null);
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース5 タイトル入力漏れ
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError2() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle(null);
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース6 タイトル101文字以上
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError3() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("あ".repeat(101));
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース7 内容入力漏れ
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError4() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent(null);
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース8 内容601文字以上
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError5() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("あ".repeat(601));
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース9 ファイル名101文字以上
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError6() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/あああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "あ".repeat(100) + ".jpg", "image/jpeg", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース10 画像ファイル以外が選択
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError7() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/あいうえお.txt");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "あいうえお.txt", "text/plain", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース11 ファイルサイズが5MBより大きい
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError8() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/9MB");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "9MB", "application/octet-stream", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }


    // テストケース12 2ページ以上のPDF
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError9() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/設計書.pdf");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "設計書.pdf", "application/pdf", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース13 既に登録されている日付(画像なし)
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError10() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.now());
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // テストケース14 既に登録されている日付(画像あり)
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testAddError11() throws Exception {

        Employee employee = new Employee();
        employee.setCode("1");
        employee.setName("煌木　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.ADMIN;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setReportDate(LocalDate.now());
        report.setTitle("煌木　太郎の新規登録テスト、タイトル");
        report.setContent("煌木　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/dog2.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "dog2.jpg", "image/jpeg", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/add")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/new"));
    }

    // 日報更新画面
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testEdit() throws Exception{

        // HTTPリクエストに対するレスポンスの検証
        MvcResult result = mockMvc.perform(get("/reports/1/update")) // URLにアクセス
                .andExpect(status().isOk()) // ステータスを確認
                .andExpect(model().attributeExists("report")) // Modelの内容を確認
                .andExpect(model().hasNoErrors()) // Modelのエラー有無の確認
                .andExpect(view().name("reports/update")) // viewの確認
                .andReturn(); // 内容の取得

        Report report = (Report) result.getModelAndView().getModel().get("report");
        assertTrue(report.getReportDate().equals(LocalDate.now()));
        assertEquals(report.getTitle(), "煌木　太郎の記載、タイトル");
        assertEquals(report.getContent(),"煌木　太郎の記載、内容");
        assertEquals(report.getEmployee().getCode(), "1");
        assertEquals(report.getImageFileName(), "dog.jpeg");
        assertEquals(report.getImageFilePath(), "/image/1/dog.jpeg");
        assertFalse(report.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可
    }

    // 日報更新処理
    // テストケース1 画像ファイルなし正常終了
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateSuccess1() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(null);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(redirectedUrl("/reports"));
    }

    // テストケース2 画像ファイル(JPEG)あり正常終了
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateSuccess2() throws Exception {

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の新規登録テスト、タイトル");
        report.setContent("田中　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/dog2.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "dog2.jpg", "image/jpeg", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(redirectedUrl("/reports"));
    }

    // テストケース3 画像ファイル(PDF)あり正常終了
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateSuccess3() throws Exception {

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の新規登録テスト、タイトル");
        report.setContent("田中　太郎の新規登録テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/画面遷移図.pdf");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "画面遷移図.pdf", "application/pdf", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(redirectedUrl("/reports"));
    }

    // テストケース4 日付入力漏れ
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError1() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(null);
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース5 タイトル入力漏れ
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError2() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle(null);
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース6 タイトル101文字以上
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError3() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("あ".repeat(101));
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース7 内容入力漏れ
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError4() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent(null);
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース8 内容601文字以上
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError5() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("あ".repeat(601));
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース9 ファイル名101文字以上
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError6() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/あああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "あ".repeat(100) + ".jpg", "image/jpeg", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース10 画像ファイル以外が選択
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError7() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/あいうえお.txt");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "あいうえお.txt", "text/plain", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース11 ファイルサイズが5MBより大きい
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError8() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/9MB");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "9MB", "application/octet-stream", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース12 2ページ以上のPDF
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError9() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(2);
        report.setReportDate(LocalDate.parse("20250225", DateTimeFormatter.ofPattern("yyyyMMdd")));
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/設計書.pdf");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "設計書.pdf", "application/pdf", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/2/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース13 既に登録されている日付(画像なし)
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError10() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(3);
        report.setReportDate(LocalDate.now());
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        InputStream is = InputStream.nullInputStream() ;
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "", "application/octet-stream", is);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/3/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }

    // テストケース14 既に登録されている日付(画像あり)
    @Test
    @WithMockUser(authorities = "GENERAL")
    @Transactional
    void testUpdateError11() throws Exception{

        Employee employee = new Employee();
        employee.setCode("2");
        employee.setName("田中　太郎");
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(false);
        employee.setPassword("");
        Role role = Role.GENERAL;
        employee.setRole(role);

        UserDetail userDetail = new UserDetail(employee);

        Report report = new Report();
        report.setId(3);
        report.setReportDate(LocalDate.now());
        report.setTitle("田中　太郎の更新テスト、タイトル");
        report.setContent("田中　太郎の更新テスト、内容");
        report.setEmployee(employee);
        report.setImageFileName(null);
        report.setImageFilePath(null);

        byte[] fileImage = null;
        File upfile = new File("./static/test/image/dog2.jpg");
        Path path = Paths.get(upfile.getAbsolutePath());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            fileImage = Files.readAllBytes(path);
        }

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "dog2.jpg", "image/jpeg", fileImage);

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(MockMvcRequestBuilders.multipart("/reports/3/update")
                .file(imageFile)
                .flashAttr("report", report)
                .with(user(userDetail))
                .with(csrf()))
                .andExpect(view().name("reports/update"));
    }


    // 画像ファイル削除処理 正常終了
    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testDeleteImage() throws Exception {

        // HTTPリクエストに対するレスポンスの検証
        mockMvc.perform(post("/reports/1/delete_image")
                .param("imageFileName", "dog.jpeg")
                .with(csrf()))
                .andExpect(redirectedUrl("/reports/1/update"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @Transactional
    void testDownload() throws Exception {
        // HTTPリクエストに対するレスポンスの検証
        MvcResult result = mockMvc.perform(post("/reports/2/download")
                .with(csrf()))
                .andReturn();

        LocalDate currentDate = LocalDate.now();
        String fileName = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_田中　太郎.csv";
        String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=UTF-8''%s";
        String outputFileName = String.format(CONTENT_DISPOSITION_FORMAT, URLEncoder.encode(fileName, StandardCharsets.UTF_8),
                UriUtils.encode(fileName, StandardCharsets.UTF_8.name()));

        MockHttpServletResponse response = (MockHttpServletResponse) result.getResponse();
        assertEquals(response.getContentType(), "text/csv");
        assertEquals(response.getHeader(HttpHeaders.CONTENT_DISPOSITION), outputFileName);

    }

}
