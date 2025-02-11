package com.techacademy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Giver;
import com.techacademy.entity.Reaction;
import com.techacademy.repository.GiverRepository;


@SpringBootTest
@ExtendWith(SpringExtension.class)
class GiverServiceTest {

    @Mock
    private GiverRepository giverRepository;

    @InjectMocks
    private GiverService giverService;

    @BeforeEach
    private void beforeEach() {
        // 戻り値の作成
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");
        Employee employeeCode3 = new Employee();
        employeeCode3.setCode("3");
        Reaction reactionId2 = new Reaction();
        reactionId2.setId(2);
        Reaction reactionId3 = new Reaction();
        reactionId3.setId(3);

        Giver giverId1 = new Giver();
        giverId1.setId(1);
        giverId1.setEmployee(employeeCode1);
        giverId1.setReaction(reactionId2);
        Giver giverId2 = new Giver();
        giverId2.setId(2);
        giverId2.setEmployee(employeeCode1);
        giverId2.setReaction(reactionId3);
        Giver giverId3 = new Giver();
        giverId3.setId(3);
        giverId3.setEmployee(employeeCode3);
        giverId3.setReaction(reactionId3);

        List<Giver> giverList = new ArrayList<Giver>();
        giverList.add(giverId1);
        giverList.add(giverId2);
        giverList.add(giverId3);

        // スタブを設定
        Mockito.when(giverRepository.findById(1)).thenReturn(Optional.of(giverId1));
        Mockito.when(giverRepository.findAll()).thenReturn(giverList);
    }


    @Test
    void testSave() {
        // giverRepositoryのsaveメソッドが問題なく終了するようスタブを設定
        Mockito.when(giverRepository.save(any())).thenReturn(null);

        Employee employeeCode3 = new Employee();
        employeeCode3.setCode("3");
        Reaction reactionId1 = new Reaction();
        reactionId1.setId(1);
        ErrorKinds result = giverService.save(employeeCode3, reactionId1);

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // giverRepositoryのsaveメソッドが1回呼ばれたことを確認する
        Mockito.verify(giverRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        // giverRepositoryのdeleteメソッドが問題なく終了するようスタブを設定
        Mockito.doNothing().when(giverRepository).delete(any());

        ErrorKinds result = giverService.delete(1);

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // giverRepositoryのsaveメソッドが1回呼ばれたことを確認する
        Mockito.verify(giverRepository, times(1)).delete(any());
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

        Giver giverId3 = givers.stream().filter(e -> e.getId().equals(3)).findFirst().get();
        assertEquals(giverId3.getId(), 3);
        assertEquals(giverId3.getEmployee().getCode(), "3");
        assertEquals(giverId3.getReaction().getId(), 3);
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
        List<Giver> givers = giverService.findByReaction(3);

        Giver giverId2 = givers.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertEquals(giverId2.getId(), 2);
        assertEquals(giverId2.getEmployee().getCode(), "1");
        assertEquals(giverId2.getReaction().getId(), 3);

        Giver giverId3 = givers.stream().filter(e -> e.getId().equals(3)).findFirst().get();
        assertEquals(giverId3.getId(), 3);
        assertEquals(giverId3.getEmployee().getCode(), "3");
        assertEquals(giverId3.getReaction().getId(), 3);

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
