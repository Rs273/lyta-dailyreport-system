package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Comment;
import com.techacademy.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // コメント保存
    @Transactional
    public void save(Comment comment) {

        comment.setEditingFlg(false);
        comment.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        commentRepository.save(comment);

        return;
    }

    // コメント内容の更新
    @Transactional
    public ErrorKinds update(Comment comment) {

        // コメントの長さチェック
        ErrorKinds result = commentRangeCheck(comment);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        Comment commentInDb = findById(comment.getId());

        comment.setEmployee(commentInDb.getEmployee());
        comment.setReport(commentInDb.getReport());
        comment.setEditingFlg(false);
        comment.setDeleteFlg(false);
        comment.setCreatedAt(commentInDb.getCreatedAt());

        if(commentInDb.getContent().equals(comment.getContent())) {
            // コメントの内容が変わらない場合
            comment.setUpdatedAt(commentInDb.getUpdatedAt());
        } else {
            // コメントの内容が異なる場合
            LocalDateTime now = LocalDateTime.now();
            comment.setUpdatedAt(now);
        }

        commentRepository.save(comment);

        return ErrorKinds.SUCCESS;
    }

    // コメント論理削除
    @Transactional
    public void delete(Integer id) {

        Comment comment = findById(id);
        LocalDateTime now = LocalDateTime.now();
        comment.setUpdatedAt(now);
        comment.setDeleteFlg(true);

        return;
    }

    // 編集中フラグの変更
    @Transactional
    public void changeEditingFlg(Integer id) {

        Comment comment = findById(id);
        if(comment.isEditingFlg()) {
            comment.setEditingFlg(false);
        } else {
            comment.setEditingFlg(true);
        }
        commentRepository.save(comment);

        return;
    }

    // 全てのコメントの編集中フラグをfalseにする
    @Transactional
    public void setFalseToEditingFlg() {
        List<Comment> comments = findAll();

        for(Comment comment: comments) {
            if(comment.isEditingFlg()) {
                comment.setEditingFlg(false);
                commentRepository.save(comment);
            }
        }

        return;
    }

    // コメント一覧表示処理
    public List<Comment> findAll(){
        return commentRepository.findAll();
    }

    // 1件を検索
    public Comment findById(Integer id) {
        // findByIdで検索
        Optional<Comment> option = commentRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Comment comment = option.orElse(null);
        return comment;
    }

    // 指定された日報のコメント一覧を返す
    public List<Comment> findByReport(Integer reportId){
        List<Comment> comments = findAll();
        List<Comment> result = new ArrayList<Comment>();

        for(Comment comment : comments) {
            if(comment.getReport().getId().equals(reportId)) {
                result.add(comment);
            }
        }

        return result;

    }

    // コメントの長さチェック
    private ErrorKinds commentRangeCheck(Comment comment) {

        int commentLength = comment.getContent().length();
        if(commentLength < 1 || 600 < commentLength) {
            return ErrorKinds.COMMENTCHEK_ERROR;
        }

        return ErrorKinds.CHECK_OK;
    }
}
