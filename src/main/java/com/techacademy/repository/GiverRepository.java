package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Giver;

public interface GiverRepository  extends JpaRepository<Giver, Integer>{

}
