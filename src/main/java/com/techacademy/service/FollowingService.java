package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Following;
import com.techacademy.repository.FollowingRepository;

@Service
public class FollowingService {


    private final FollowingRepository followingRepository;

    public FollowingService(FollowingRepository followingRepository) {
        this.followingRepository = followingRepository;
    }

    @Transactional
    public ErrorKinds save(Employee followerEmployee, Employee followingEmployee) {

        Following following = new Following();

        following.setFollowerEmployee(followerEmployee);
        following.setFollowingEmployee(followingEmployee);

        following.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        following.setCreatedAt(now);
        following.setUpdatedAt(now);

        followingRepository.save(following);

        return ErrorKinds.SUCCESS;
    }

    @Transactional
    public ErrorKinds delete(String followerEmployeeCode, String followingEmployeeCode) {

        Following following = findByFollowerAndFollowing(followerEmployeeCode, followingEmployeeCode);

        LocalDateTime now = LocalDateTime.now();
        following.setUpdatedAt(now);
        following.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    public List<Following> findAll(){
        return followingRepository.findAll();
    }

    public Following findById(Integer id) {
        Optional<Following> option = followingRepository.findById(id);
        Following following = option.orElse(null);
        return following;
    }

    public List<Following> findByFollower(String followerEmployeeCode){
        List<Following> followingList = findAll();
        List<Following> result = new ArrayList<Following>();

        for(Following follower: followingList) {
            if(follower.getFollowerEmployee().getCode().equals(followerEmployeeCode)) {
                result.add(follower);
            }
        }

        return result;
    }

    public Following findByFollowerAndFollowing(String followerEmployeeCode, String followingEmployeeCode) {
        List<Following> followingList = findAll();

        for(Following follower: followingList) {
            if(follower.getFollowerEmployee().getCode().equals(followerEmployeeCode)) {
                if(follower.getFollowingEmployee().getCode().equals(followingEmployeeCode)) {
                    return follower;
                }
            }
        }

        return null;
    }
}
