package com.techacademy.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reactions")
public class Reaction {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 絵文字
    @Column(length = 10, nullable = false)
    private String emoji;

    // 数
    @Column(nullable = false)
    private Integer count;

    // 日報ID
    @ManyToOne
    @JoinColumn(name = "report_id", referencedColumnName = "id", nullable = false)
    private Report report;

    @OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL)
    private List<Giver> giverList;
}
