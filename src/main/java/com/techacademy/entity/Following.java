package com.techacademy.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "followings")
@SQLRestriction("delete_flg = false")
public class Following {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // フォロワー従業員コード
    @ManyToOne
    @JoinColumn(name = "follower_employee_code", referencedColumnName = "code", nullable = false)
    private Employee followerEmployee;

    // フォロー中従業員コード
    @ManyToOne
    @JoinColumn(name = "following_employee_code", referencedColumnName = "code", nullable = false)
    private Employee followingEmployee;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
