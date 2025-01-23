package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Integer>{

}
