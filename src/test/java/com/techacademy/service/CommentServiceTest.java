package com.techacademy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
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

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Comment;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.CommentRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    private void beforeEach() {
        // 戻り値を作成
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");
        Report reportId1 = new Report();
        reportId1.setId(1);
        Comment commentId1 = new Comment();
        commentId1.setId(1);
        commentId1.setContent("煌木　太郎のコメント、内容。");
        commentId1.setEmployee(employeeCode1);
        commentId1.setReport(reportId1);
        commentId1.setEditingFlg(false);
        commentId1.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        commentId1.setCreatedAt(now);
        commentId1.setUpdatedAt(now);

        Employee employeeCode3 = new Employee();
        employeeCode3.setCode("3");
        Report reportId2 = new Report();
        reportId2.setId(2);
        Comment commentId2 = new Comment();
        commentId2.setId(2);
        commentId2.setContent("佐藤　太郎のコメント、内容。");
        commentId2.setEmployee(employeeCode3);
        commentId2.setReport(reportId2);
        commentId2.setEditingFlg(false);
        commentId2.setDeleteFlg(false);
        commentId2.setCreatedAt(now);
        commentId2.setUpdatedAt(now);

        List<Comment> commentList = new ArrayList<Comment>();
        commentList.add(commentId1);
        commentList.add(commentId2);

        // スタブを設定
        Mockito.when(commentRepository.findById(1)).thenReturn(Optional.of(commentId1));
        Mockito.when(commentRepository.findAll()).thenReturn(commentList);
    }

    @Test
    void testSave() {
        // commentRepostioryのsaveメソッドが問題なく終了するようスタブを設定
        Mockito.when(commentRepository.save(any())).thenReturn(null);

        // 新たなコメントを作成する
        Comment comment = new Comment();
        comment.setContent("煌木　太郎の新しいコメント、内容。");
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");
        comment.setEmployee(employeeCode1);
        Report reportId1 = new Report();
        reportId1.setId(1);
        comment.setReport(reportId1);

        // コメントを保存する
        ErrorKinds result = commentService.save(comment);
        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // commentRepositoryのsaveメソッドが1回呼ばれたことを確認する
        Mockito.verify(commentRepository, times(1)).save(any());
    }

    @Test
    void testUpdate() {
        // commentRepostioryのsaveメソッドが問題なく終了するようスタブを設定
        Mockito.when(commentRepository.save(any())).thenReturn(null);

        // コメントの内容が変わらない場合
        Comment noChangeComment = new Comment();
        noChangeComment.setId(1);
        noChangeComment.setContent("煌木　太郎のコメント、内容。");
        // コメントを更新する
        ErrorKinds result1 = commentService.update(noChangeComment);
        // エラーがなく更新ができたかを確認する
        assertEquals(result1, ErrorKinds.SUCCESS);

        // commentRepositoryのsaveメソッドが1回目が呼ばれたことを確認する
        Mockito.verify(commentRepository, times(1)).save(any());

        // コメントを空白にして更新
        Comment blankComment = new Comment();
        blankComment.setId(1);
        blankComment.setContent("");
        // コメントを更新する
        ErrorKinds result2 = commentService.update(blankComment);
        // エラーが発生したかを確認する
        assertEquals(result2, ErrorKinds.COMMENTCHEK_ERROR);

        // コメントを1文字にして更新
        Comment comment = new Comment();
        comment.setId(1);
        comment.setContent("あ");
        // コメントを更新する
        ErrorKinds result3 = commentService.update(comment);
        // エラーがなく更新ができたかを確認する
        assertEquals(result3, ErrorKinds.SUCCESS);

        // commentRepositoryのsaveメソッドが2回目が呼ばれたことを確認する
        Mockito.verify(commentRepository, times(2)).save(any());

        // コメントを600文字にして更新
        Comment comment2 = new Comment();
        comment2.setId(1);
        comment2.setContent("ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ");
        // コメントを更新する
        ErrorKinds result4 = commentService.update(comment2);
        // エラーがなく更新ができたかを確認する
        assertEquals(result4, ErrorKinds.SUCCESS);

        // commentRepositoryのsaveメソッドが3回目が呼ばれたことを確認する
        Mockito.verify(commentRepository, times(3)).save(any());

        // コメントを601文字にして更新
        Comment longComment = new Comment();
        longComment.setId(1);
        longComment.setContent("あああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ");
        // コメントを更新する
        ErrorKinds result5 = commentService.update(longComment);
        // エラーが発生したかを確認する
        assertEquals(result5, ErrorKinds.COMMENTCHEK_ERROR);

    }

    @Test
    void testDelete() {
        // id1のコメントを削除する
        ErrorKinds result = commentService.delete(1);

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
    }

    @Test
    void testChangeEditingFlg() {
        // id1のコメントの編集中フラグをfalse→trueにする
        ErrorKinds result1 = commentService.changeEditingFlg(1);

        // ErrorKindsがSUCCESSであることを確認する
        assertEquals(result1, ErrorKinds.SUCCESS);
        // commentRepositoryのsaveメソッドが1回目が呼ばれたことを確認する
        Mockito.verify(commentRepository, times(1)).save(any());

        // id1のコメントの編集中フラグをtrue→falseにする
        Comment returnComment = new Comment();
        returnComment.setId(1);
        returnComment.setContent("煌木　太郎のコメント、内容。");
        returnComment.setEditingFlg(true);
        returnComment.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        returnComment.setCreatedAt(now);
        returnComment.setUpdatedAt(now);

        Mockito.when(commentRepository.findById(1)).thenReturn(Optional.of(returnComment));

        ErrorKinds result2 = commentService.changeEditingFlg(1);

        // resultがSUCCESSであることを確認する
        assertEquals(result2, ErrorKinds.SUCCESS);
        // commentRepositoryのsaveメソッドが2回目が呼ばれたことを確認する
        Mockito.verify(commentRepository, times(2)).save(any());
    }

    @Test
    void testSetFalseToEditingFlg() {

        // 編集中フラグがtrueの戻り値を
        Comment CommentId1 = new Comment();
        CommentId1.setId(1);
        CommentId1.setContent("煌木　太郎のコメント、内容。");
        CommentId1.setEditingFlg(true);
        CommentId1.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        CommentId1.setCreatedAt(now);
        CommentId1.setUpdatedAt(now);

        Comment commentId2 = new Comment();
        commentId2.setId(2);
        commentId2.setContent("佐藤　太郎のコメント、内容。");
        commentId2.setEditingFlg(false);
        commentId2.setDeleteFlg(false);
        commentId2.setCreatedAt(now);
        commentId2.setUpdatedAt(now);

        List<Comment> commentList = new ArrayList<Comment>();
        commentList.add(CommentId1);
        commentList.add(commentId2);

        Mockito.when(commentRepository.findAll()).thenReturn(commentList);

        // 全てのコメントの編集中フラグをfalseにする
        ErrorKinds result = commentService.setFalseToEditingFlg();

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // commentRepositoryのsaveメソッドが1回呼ばれたことを確認する
        Mockito.verify(commentRepository, times(1)).save(any());
    }

    @Test
    void testFindAll() {
        List<Comment> comments = commentService.findAll();

        Comment commentId1 = comments.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(commentId1.getId(), 1);
        assertEquals(commentId1.getContent(), "煌木　太郎のコメント、内容。");
        assertEquals(commentId1.getEmployee().getCode(), "1");
        assertEquals(commentId1.getReport().getId(), 1);
        assertFalse(commentId1.isEditingFlg());
        assertFalse(commentId1.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        Comment commentId2 = comments.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertEquals(commentId2.getId(), 2);
        assertEquals(commentId2.getContent(), "佐藤　太郎のコメント、内容。");
        assertEquals(commentId2.getEmployee().getCode(), "3");
        assertEquals(commentId2.getReport().getId(), 2);
        assertFalse(commentId2.isEditingFlg());
        assertFalse(commentId2.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

    }

    @Test
    void testFindById() {
        Comment comment = commentService.findById(1);
        assertEquals(comment.getId(), 1);
        assertEquals(comment.getContent(), "煌木　太郎のコメント、内容。");
        assertEquals(comment.getEmployee().getCode(), "1");
        assertEquals(comment.getReport().getId(), 1);
        assertFalse(comment.isEditingFlg());
        assertFalse(comment.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        Comment commentNull = commentService.findById(100);
        assertEquals(commentNull, null);
    }

    @Test
    void testFindByReport() {
        List<Comment> comments = commentService.findByReport(1);

        Comment commentId1 = comments.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(commentId1.getId(), 1);
        assertEquals(commentId1.getContent(), "煌木　太郎のコメント、内容。");
        assertEquals(commentId1.getEmployee().getCode(), "1");
        assertEquals(commentId1.getReport().getId(), 1);
        assertFalse(commentId1.isEditingFlg());
        assertFalse(commentId1.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        List<Comment> commentEmpty = commentService.findByReport(100);
        assertEquals(commentEmpty.size(), 0);
    }

}
