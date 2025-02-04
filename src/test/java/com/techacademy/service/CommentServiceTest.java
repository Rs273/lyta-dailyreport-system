package com.techacademy.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Comment;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ReportService reportService;

    @Test
    void testSave() {
        // 新たなコメントを作成する
        Comment comment = new Comment();
        comment.setContent("煌木　太郎の新しいコメント、内容。");
        comment.setEmployee(employeeService.findByCode("1"));
        comment.setReport(reportService.findById(2));
        // コメントを保存する
        commentService.save(comment);

        // 保存したコメントを確認する
        Comment savedComment = commentService.findById(5);
        assertEquals(comment.getId(), 5);
        assertEquals(comment.getContent(), "煌木　太郎の新しいコメント、内容。");
        assertEquals(comment.getEmployee().getCode(), "1");
        assertEquals(comment.getReport().getId(), 2);
        assertFalse(comment.isEditingFlg());
        assertFalse(comment.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

    }

    @Test
    void testUpdate() {
        // コメントの内容が変わらない場合
        Comment noChangeComment = new Comment();
        noChangeComment.setId(4);
        noChangeComment.setContent("佐藤　太郎の更新用コメント、内容。");
        // コメントを更新する
        ErrorKinds result1 = commentService.update(noChangeComment);
        // エラーがなく更新ができたかを確認する
        assertEquals(result1, ErrorKinds.SUCCESS);
        // 更新したコメントを確認する
        Comment updatedComment1 = commentService.findById(4);
        assertEquals(updatedComment1.getId(), 4);
        assertEquals(updatedComment1.getContent(), "佐藤　太郎の更新用コメント、内容。");
        assertEquals(updatedComment1.getEmployee().getCode(), "3");
        assertEquals(updatedComment1.getReport().getId(), 2);
        assertFalse(updatedComment1.isEditingFlg());
        assertFalse(updatedComment1.isDeleteFlg());

        // コメントを空白にして更新
        Comment blankComment = new Comment();
        blankComment.setId(4);
        blankComment.setContent("");
        // コメントを更新する
        ErrorKinds result2 = commentService.update(blankComment);
        // エラーが発生したかを確認する
        assertEquals(result2, ErrorKinds.COMMENTCHEK_ERROR);

        // コメントを1文字にして更新
        Comment comment = new Comment();
        comment.setId(4);
        comment.setContent("あ");
        // コメントを更新する
        ErrorKinds result3 = commentService.update(comment);
        // エラーがなく更新ができたかを確認する
        assertEquals(result3, ErrorKinds.SUCCESS);
        // 更新したコメントを確認する
        Comment updatedComment2 = commentService.findById(4);
        assertEquals(updatedComment2.getId(), 4);
        assertEquals(updatedComment2.getContent(), "あ");
        assertEquals(updatedComment2.getEmployee().getCode(), "3");
        assertEquals(updatedComment2.getReport().getId(), 2);
        assertFalse(updatedComment2.isEditingFlg());
        assertFalse(updatedComment2.isDeleteFlg());

        // コメントを600文字にして更新
        Comment comment2 = new Comment();
        comment2.setId(4);
        comment2.setContent("ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ");
        // コメントを更新する
        ErrorKinds result4 = commentService.update(comment2);
        // エラーがなく更新ができたかを確認する
        assertEquals(result4, ErrorKinds.SUCCESS);
        // 更新したコメントを確認する
        Comment updatedComment3 = commentService.findById(4);
        assertEquals(updatedComment3.getId(), 4);
        assertEquals(updatedComment3.getContent(), "ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ");
        assertEquals(updatedComment3.getEmployee().getCode(), "3");
        assertEquals(updatedComment3.getReport().getId(), 2);
        assertFalse(updatedComment3.isEditingFlg());
        assertFalse(updatedComment3.isDeleteFlg());

        // コメントを601文字にして更新
        Comment longComment = new Comment();
        longComment.setId(4);
        longComment.setContent("あああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ");
        // コメントを更新する
        ErrorKinds result5 = commentService.update(longComment);
        // エラーが発生したかを確認する
        assertEquals(result5, ErrorKinds.COMMENTCHEK_ERROR);

    }

    @Test
    void testDelete() {
        // id3のコメントを削除する
        commentService.delete(3);

        // コメントが削除されたことを確認する
        Comment commentNull = commentService.findById(3);
        assertEquals(commentNull, null);
    }

    @Test
    void testChangeEditingFlg() {
        // id1のコメントの編集中フラグをtrueにする
        commentService.changeEditingFlg(1);
        // コメントの内容を確認する
        Comment trueComment = commentService.findById(1);
        assertEquals(trueComment.getId(), 1);
        assertEquals(trueComment.getContent(), "煌木　太郎のコメント、内容。");
        assertEquals(trueComment.getEmployee().getCode(), "1");
        assertEquals(trueComment.getReport().getId(), 1);
        assertTrue(trueComment.isEditingFlg());
        assertFalse(trueComment.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可

        // id1のコメントの編集中フラグをfalseにする
        commentService.changeEditingFlg(1);
        // コメントの内容を確認する
        Comment falseComment = commentService.findById(1);
        assertEquals(falseComment.getId(), 1);
        assertEquals(falseComment.getContent(), "煌木　太郎のコメント、内容。");
        assertEquals(falseComment.getEmployee().getCode(), "1");
        assertEquals(falseComment.getReport().getId(), 1);
        assertFalse(falseComment.isEditingFlg());
        assertFalse(falseComment.isDeleteFlg());
        // 登録日付、更新日付はミリ秒単位での結果比較となるためテストでの確認不可
    }

    @Test
    void testSetFalseToEditingFlg() {
        // id1のコメントの編集中フラグをtrueにする
        commentService.changeEditingFlg(1);

        // 全てのコメントの編集中フラグをfalseにする
        commentService.setFalseToEditingFlg();

        // 全てのコメントの内容を確認する
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
