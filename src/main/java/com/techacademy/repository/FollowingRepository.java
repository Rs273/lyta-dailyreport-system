package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Following;

public interface FollowingRepository extends JpaRepository<Following, Integer> {

}
