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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Giver;
import com.techacademy.entity.Reaction;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReactionRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private GiverService giverService;

    @InjectMocks
    private ReactionService reactionService;

    @BeforeEach
    private void beforeEach() {
        // 戻り値の作成
        // 日報
        Report reportId1 = new Report();
        reportId1.setId(1);
        Report reportId2 = new Report();
        reportId2.setId(2);
        // リアクション
        Reaction reactionId1 = new Reaction();
        reactionId1.setId(1);
        reactionId1.setEmoji("👍");
        reactionId1.setCount(0);
        reactionId1.setReport(reportId1);
        Reaction reactionId2 = new Reaction();
        reactionId2.setId(2);
        reactionId2.setEmoji("✅");
        reactionId2.setCount(1);
        reactionId2.setReport(reportId1);
        Reaction reactionId3 = new Reaction();
        reactionId3.setId(3);
        reactionId3.setEmoji("💪");
        reactionId3.setCount(2);
        reactionId3.setReport(reportId1);
        Reaction reactionId4 = new Reaction();
        reactionId4.setId(4);
        reactionId4.setEmoji("👀");
        reactionId4.setCount(0);
        reactionId4.setReport(reportId1);
        Reaction reactionId5 = new Reaction();
        reactionId5.setId(5);
        reactionId5.setEmoji("🙌");
        reactionId5.setCount(0);
        reactionId5.setReport(reportId1);
        Reaction reactionId6 = new Reaction();
        reactionId6.setId(6);
        reactionId6.setEmoji("👍");
        reactionId6.setCount(0);
        reactionId6.setReport(reportId2);
        Reaction reactionId7 = new Reaction();
        reactionId7.setId(7);
        reactionId7.setEmoji("✅");
        reactionId7.setCount(0);
        reactionId7.setReport(reportId2);
        Reaction reactionId8 = new Reaction();
        reactionId8.setId(8);
        reactionId8.setEmoji("💪");
        reactionId8.setCount(0);
        reactionId8.setReport(reportId2);
        Reaction reactionId9 = new Reaction();
        reactionId9.setId(9);
        reactionId9.setEmoji("👀");
        reactionId9.setCount(0);
        reactionId9.setReport(reportId2);
        Reaction reactionId10 = new Reaction();
        reactionId10.setId(10);
        reactionId10.setEmoji("🙌");
        reactionId10.setCount(0);
        reactionId10.setReport(reportId2);
        List<Reaction> reactionList = new ArrayList<Reaction>();
        reactionList.add(reactionId1);
        reactionList.add(reactionId2);
        reactionList.add(reactionId3);
        reactionList.add(reactionId4);
        reactionList.add(reactionId5);
        reactionList.add(reactionId6);
        reactionList.add(reactionId7);
        reactionList.add(reactionId8);
        reactionList.add(reactionId9);
        reactionList.add(reactionId10);

        // スタブを設定
        Mockito.when(reactionRepository.findById(1)).thenReturn(Optional.of(reactionId1));
        Mockito.when(reactionRepository.findAll()).thenReturn(reactionList);
    }

    @Test
    void testSave() {
        // reactionRepositoryのsaveメソッドが問題なく終了するようスタブを設定
        Mockito.when(reactionRepository.save(any())).thenReturn(null);
        // 絵文字ピースのID1の日報のリアクションをセーブする
        Report reportId1 = new Report();
        reportId1.setId(1);
        ErrorKinds result = reactionService.save("✌️", reportId1);

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // reactionRepositoryのsaveメソッドが1回呼ばれたことを確認する
        Mockito.verify(reactionRepository, times(1)).save(any());
    }

    @Test
    void testSaveAll() {
        // reactionRepositoryのsaveメソッドが問題なく終了するようスタブを設定
        Mockito.when(reactionRepository.save(any())).thenReturn(null);
        // ID1の日報に対応するリアクションを作成する
        Report reportId1 = new Report();
        reportId1.setId(1);
        ErrorKinds result = reactionService.saveAll(reportId1);

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // reactionRepositoryのsaveメソッドが5回呼ばれたことを確認する
        Mockito.verify(reactionRepository, times(5)).save(any());
    }

    @Test
    void testUpdate() {
        // 戻り値を作成
        // 従業員
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");
        Employee employeeCode3 = new Employee();
        employeeCode3.setCode("3");
        // 日報
        Report reportId1 = new Report();
        reportId1.setId(1);
        // リアクション
        Reaction reactionId3 = new Reaction();
        reactionId3.setId(3);
        reactionId3.setEmoji("💪");
        reactionId3.setCount(2);
        reactionId3.setReport(reportId1);
        // リアクション付与者
        Giver giverId2 = new Giver();
        giverId2.setId(2);
        giverId2.setEmployee(employeeCode1);
        giverId2.setReaction(reactionId3);
        Giver giverId3 = new Giver();
        giverId3.setId(3);
        giverId3.setEmployee(employeeCode3);
        giverId3.setReaction(reactionId3);
        List<Giver> giverList = new ArrayList<Giver>();
        giverList.add(giverId2);
        giverList.add(giverId3);

        // スタブを設定
        Mockito.when(reactionRepository.findById(3)).thenReturn(Optional.of(reactionId3));
        Mockito.when(giverService.findByReaction(3)).thenReturn(giverList);

        // reaction_idが3のリアクションを更新する（countが-1される）
        ErrorKinds result1 = reactionService.update(3, employeeCode1);

        // resultがSUCCESSであることを確認する
        assertEquals(result1, ErrorKinds.SUCCESS);
        // reactionRepositoryのsaveメソッドが1回目が呼ばれたことを確認する
        Mockito.verify(reactionRepository, times(1)).save(any());

        // countが-1された状態に変更する
        reactionId3.setCount(1);
        giverList.clear();
        giverList.add(giverId3);

        // もう一度同じユーザーでリアクションを更新する（countが+1される）
        ErrorKinds result2 = reactionService.update(3, employeeCode1);

        // resultがSUCCESSであることを確認する
        assertEquals(result2, ErrorKinds.SUCCESS);
        // reactionRepositoryのsaveメソッドが2回目が呼ばれたことを確認する
        Mockito.verify(reactionRepository, times(2)).save(any());
    }

    @Test
    void testDelete() {
        // reactionRepositoryのdeleteメソッドが問題なく終了するようスタブを設定
        Mockito.doNothing().when(reactionRepository).delete(any());
        // reaction_idが1のリアクションを削除する
        ErrorKinds result = reactionService.delete(1);

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // reactionRepositoryのdeleteメソッドが1回呼ばれたことを確認する
        Mockito.verify(reactionRepository, times(1)).delete(any());
    }

    @Test
    void testDeleteAll() {
        // reactionRepositoryのdeleteメソッドが問題なく終了するようスタブを設定
        Mockito.doNothing().when(reactionRepository).delete(any());

        // 戻り値の作成
        // 従業員
        Employee employeeCode1 = new Employee();
        employeeCode1.setCode("1");
        // 日報
        Report reportId1 = new Report();
        reportId1.setId(1);
        // リアクション
        Reaction reactionId2 = new Reaction();
        reactionId2.setId(2);
        reactionId2.setEmoji("✅");
        reactionId2.setCount(1);
        reactionId2.setReport(reportId1);
        // リアクション付与者
        Giver giverId1 = new Giver();
        giverId1.setId(1);
        giverId1.setEmployee(employeeCode1);
        giverId1.setReaction(reactionId2);
        List<Giver> giverList = new ArrayList<Giver>();
        giverList.add(giverId1);
        // スタブを設定
        Mockito.when(reactionRepository.findById(2)).thenReturn(Optional.of(reactionId2));
        Mockito.when(giverService.findByReaction(2)).thenReturn(giverList);

        // ID1の日報に対応するリアクションを全て消す
        ErrorKinds result = reactionService.deleteAll(1);

        // resultがSUCCESSであることを確認する
        assertEquals(result, ErrorKinds.SUCCESS);
        // reactionRepositoryのdeleteメソッドが5回呼ばれたことを確認する
        Mockito.verify(reactionRepository, times(5)).delete(any());
    }

    @Test
    void testFindAll() {
        List<Reaction> reactions = reactionService.findAll();

        Reaction reactionId1 = reactions.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(reactionId1.getId(), 1);
        assertEquals(reactionId1.getCount(), 0);
        assertEquals(reactionId1.getEmoji(), "👍");
        assertEquals(reactionId1.getReport().getId(), 1);

        Reaction reactionId2 = reactions.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertEquals(reactionId2.getId(), 2);
        assertEquals(reactionId2.getCount(), 1);
        assertEquals(reactionId2.getEmoji(), "✅");
        assertEquals(reactionId2.getReport().getId(), 1);

        Reaction reactionId3 = reactions.stream().filter(e -> e.getId().equals(3)).findFirst().get();
        assertEquals(reactionId3.getId(), 3);
        assertEquals(reactionId3.getCount(), 2);
        assertEquals(reactionId3.getEmoji(), "💪");
        assertEquals(reactionId3.getReport().getId(), 1);

        Reaction reactionId4 = reactions.stream().filter(e -> e.getId().equals(4)).findFirst().get();
        assertEquals(reactionId4.getId(), 4);
        assertEquals(reactionId4.getCount(), 0);
        assertEquals(reactionId4.getEmoji(), "👀");
        assertEquals(reactionId4.getReport().getId(), 1);

        Reaction reactionId5 = reactions.stream().filter(e -> e.getId().equals(5)).findFirst().get();
        assertEquals(reactionId5.getId(), 5);
        assertEquals(reactionId5.getCount(), 0);
        assertEquals(reactionId5.getEmoji(), "🙌");
        assertEquals(reactionId5.getReport().getId(), 1);

        Reaction reactionId6 = reactions.stream().filter(e -> e.getId().equals(6)).findFirst().get();
        assertEquals(reactionId6.getId(), 6);
        assertEquals(reactionId6.getCount(), 0);
        assertEquals(reactionId6.getEmoji(), "👍");
        assertEquals(reactionId6.getReport().getId(), 2);

        Reaction reactionId7 = reactions.stream().filter(e -> e.getId().equals(7)).findFirst().get();
        assertEquals(reactionId7.getId(), 7);
        assertEquals(reactionId7.getCount(), 0);
        assertEquals(reactionId7.getEmoji(), "✅");
        assertEquals(reactionId7.getReport().getId(), 2);

        Reaction reactionId8 = reactions.stream().filter(e -> e.getId().equals(8)).findFirst().get();
        assertEquals(reactionId8.getId(), 8);
        assertEquals(reactionId8.getCount(), 0);
        assertEquals(reactionId8.getEmoji(), "💪");
        assertEquals(reactionId8.getReport().getId(), 2);

        Reaction reactionId9 = reactions.stream().filter(e -> e.getId().equals(9)).findFirst().get();
        assertEquals(reactionId9.getId(), 9);
        assertEquals(reactionId9.getCount(), 0);
        assertEquals(reactionId9.getEmoji(), "👀");
        assertEquals(reactionId9.getReport().getId(), 2);

        Reaction reactionId10 = reactions.stream().filter(e -> e.getId().equals(10)).findFirst().get();
        assertEquals(reactionId10.getId(), 10);
        assertEquals(reactionId10.getCount(), 0);
        assertEquals(reactionId10.getEmoji(), "🙌");
        assertEquals(reactionId10.getReport().getId(), 2);
    }

    @Test
    void testFindById() {
        // 取得できた場合
        Reaction reaction = reactionService.findById(1);
        assertEquals(reaction.getId(), 1);
        assertEquals(reaction.getCount(), 0);
        assertEquals(reaction.getEmoji(), "👍");
        assertEquals(reaction.getReport().getId(), 1);

        // 取得できなかった場合
        Reaction reactionNull = reactionService.findById(100);
        assertEquals(reactionNull, null);
    }

    @Test
    void testFindByReport() {
        List<Reaction> reactions = reactionService.findByReport(1);

        Reaction reactionId1 = reactions.stream().filter(e -> e.getId().equals(1)).findFirst().get();
        assertEquals(reactionId1.getId(), 1);
        assertEquals(reactionId1.getCount(), 0);
        assertEquals(reactionId1.getEmoji(), "👍");
        assertEquals(reactionId1.getReport().getId(), 1);

        Reaction reactionId2 = reactions.stream().filter(e -> e.getId().equals(2)).findFirst().get();
        assertEquals(reactionId2.getId(), 2);
        assertEquals(reactionId2.getCount(), 1);
        assertEquals(reactionId2.getEmoji(), "✅");
        assertEquals(reactionId2.getReport().getId(), 1);

        Reaction reactionId3 = reactions.stream().filter(e -> e.getId().equals(3)).findFirst().get();
        assertEquals(reactionId3.getId(), 3);
        assertEquals(reactionId3.getCount(), 2);
        assertEquals(reactionId3.getEmoji(), "💪");
        assertEquals(reactionId3.getReport().getId(), 1);

        Reaction reactionId4 = reactions.stream().filter(e -> e.getId().equals(4)).findFirst().get();
        assertEquals(reactionId4.getId(), 4);
        assertEquals(reactionId4.getCount(), 0);
        assertEquals(reactionId4.getEmoji(), "👀");
        assertEquals(reactionId4.getReport().getId(), 1);

        Reaction reactionId5 = reactions.stream().filter(e -> e.getId().equals(5)).findFirst().get();
        assertEquals(reactionId5.getId(), 5);
        assertEquals(reactionId5.getCount(), 0);
        assertEquals(reactionId5.getEmoji(), "🙌");
        assertEquals(reactionId5.getReport().getId(), 1);

        // 取得できなかった場合
        List<Reaction> reactionEmpty = reactionService.findByReport(100);
        assertEquals(reactionEmpty.size(), 0);
    }

}
