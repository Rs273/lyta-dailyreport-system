package com.techacademy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Giver;
import com.techacademy.entity.Reaction;
import com.techacademy.repository.GiverRepository;

@Service
public class GiverService {

    private final GiverRepository giverRepository;
    private final EmployeeService employeeService;
    private final ReactionService reactionService;

    public GiverService(GiverRepository giverRepository, EmployeeService employeeService, ReactionService reactionService) {
        this.giverRepository = giverRepository;
        this.employeeService = employeeService;
        this.reactionService = reactionService;
    }

    @Transactional
    public void save(String employeeCode, Integer reactionId) {
        Giver giver = new Giver();

        giver.setEmployee(employeeService.findByCode(employeeCode));
        giver.setReaction(reactionService.findById(reactionId));

        giverRepository.save(giver);

        return;
    }

    @Transactional
    public void delete(Integer id) {
        Giver giver = findById(id);
        giverRepository.delete(giver);
        return;
    }

    // 全件表示
    public List<Giver> findAll() {
        return giverRepository.findAll();
    }

    // 1件検索
    public Giver findById(Integer id) {
        // findByIdで検索
        Optional<Giver> option = giverRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Giver giver = option.orElse(null);
        return giver;
    }

    public List<Giver> findByReaction(Integer reactionId){
        List<Giver> givers = findAll();
        List<Giver> result = new ArrayList<Giver>();

        for(Giver giver : givers) {
            if(giver.getReaction().getId().equals(reactionId)) {
                result.add(giver);
            }
        }

        return result;
    }
}
