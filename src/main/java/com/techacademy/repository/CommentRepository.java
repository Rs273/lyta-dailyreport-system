package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>{

}
