package com.techacademy.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.entity.Giver;


@SpringBootTest
@ExtendWith(SpringExtension.class)
class GiverServiceTest {

    @Autowired
    private GiverService giverService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private EmployeeService employeeService;

//    @BeforeAll
//    static void beforeAll() {
//        // テストデータの登録
//        reactionService.update(2, employeeService.findByCode("1"));
//        reactionService.update(3, employeeService.findByCode("1"));
//        reactionService.update(3, employeeService.findByCode("3"));
//    }


    @Test
    void testSave() {
        giverService.save(employeeService.findByCode("3"), reactionService.findById(1));

        Giver giver = giverService.findById(5);
        assertEquals(giver.getId(), 5);
        assertEquals(giver.getEmployee().getCode(), "3");
        assertEquals(giver.getReaction().getId(), 1);
    }

    @Test
    void testDelete() {
        giverService.delete(3);

        Giver giverNull = giverService.findById(3);
        assertEquals(giverNull, null);
    }

    @Test
    void testFindAll() {
        List<Giver> givers = giverService.findAll();

        Giver giverId1 = givers.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(giverId1.getId(), 1);
        assertEquals(giverId1.getEmployee().getCode(), "1");
        assertEquals(giverId1.getReaction().getId(), 2);

        Giver giverId2 = givers.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertEquals(giverId2.getId(), 2);
        assertEquals(giverId2.getEmployee().getCode(), "1");
        assertEquals(giverId2.getReaction().getId(), 3);
    }

    @Test
    void testFindById() {
        // 取得できた場合
        Giver giver = giverService.findById(1);
        assertEquals(giver.getId(), 1);
        assertEquals(giver.getEmployee().getCode(), "1");
        assertEquals(giver.getReaction().getId(), 2);

        // 取得できなかった場合
        Giver giverNull = giverService.findById(100);
        assertEquals(giverNull, null);
    }

    @Test
    void testFindByReaction() {
        // 取得できた場合
        List<Giver> givers = giverService.findByReaction(2);

        Giver giverId1 = givers.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(giverId1.getId(), 1);
        assertEquals(giverId1.getEmployee().getCode(), "1");
        assertEquals(giverId1.getReaction().getId(), 2);

        // 取得できなかった場合
        List<Giver> giverEmpty = giverService.findByReaction(100);
        assertEquals(giverEmpty.size(), 0);
    }

    @Test
    void testFindByEmployee() {
        // 取得できた場合
        List<Giver> givers = giverService.findByEmployee("1");

        Giver giverId1 = givers.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(giverId1.getId(), 1);
        assertEquals(giverId1.getEmployee().getCode(), "1");
        assertEquals(giverId1.getReaction().getId(), 2);

        Giver giverId2 = givers.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertEquals(giverId2.getId(), 2);
        assertEquals(giverId2.getEmployee().getCode(), "1");
        assertEquals(giverId2.getReaction().getId(), 3);
    }

}
