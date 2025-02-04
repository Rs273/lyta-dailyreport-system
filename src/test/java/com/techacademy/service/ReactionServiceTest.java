package com.techacademy.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.techacademy.entity.Reaction;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ReactionServiceTest {

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private GiverService giverService;

    @Test
    void testSave() {
        // 絵文字ピースのリアクションをセーブする
        reactionService.save("✌️", reportService.findById(1));

        // DBに登録されたかを確認する
        Reaction reaction = reactionService.findById(12);
        assertEquals(reaction.getId(), 12);
        assertEquals(reaction.getCount(), 0);
        assertEquals(reaction.getEmoji(), "✌️");
        assertEquals(reaction.getReport().getId(), 1);
    }

    @Test
    void testSaveAll() {
        // ID3の日報に対応するリアクションを作成する
        reactionService.saveAll(reportService.findById(3));

        // DBに登録されたかを確認する
        List<Reaction> reactions = reactionService.findByReport(3);

        Reaction reaction1 = reactions.stream().filter(e -> e.getId().equals(13)).findFirst().get();
        assertEquals(reaction1.getId(), 13);
        assertEquals(reaction1.getCount(), 0);
        assertEquals(reaction1.getEmoji(), "👍");
        assertEquals(reaction1.getReport().getId(), 3);

        Reaction reaction2 = reactions.stream().filter(e -> e.getId().equals(14)).findFirst().get();
        assertEquals(reaction2.getId(), 14);
        assertEquals(reaction2.getCount(), 0);
        assertEquals(reaction2.getEmoji(), "✅");
        assertEquals(reaction2.getReport().getId(), 3);

        Reaction reaction3 = reactions.stream().filter(e -> e.getId().equals(15)).findFirst().get();
        assertEquals(reaction3.getId(), 15);
        assertEquals(reaction3.getCount(), 0);
        assertEquals(reaction3.getEmoji(), "💪");
        assertEquals(reaction3.getReport().getId(), 3);

        Reaction reaction4 = reactions.stream().filter(e -> e.getId().equals(16)).findFirst().get();
        assertEquals(reaction4.getId(), 16);
        assertEquals(reaction4.getCount(), 0);
        assertEquals(reaction4.getEmoji(), "👀");
        assertEquals(reaction4.getReport().getId(), 3);

        Reaction reaction5 = reactions.stream().filter(e -> e.getId().equals(17)).findFirst().get();
        assertEquals(reaction5.getId(), 17);
        assertEquals(reaction5.getCount(), 0);
        assertEquals(reaction5.getEmoji(), "🙌");
        assertEquals(reaction5.getReport().getId(), 3);
    }

    @Test
    void testUpdate() {
        // reaction_idが3のリアクションを更新する（countが-1される）
        reactionService.update(3, employeeService.findByCode("1"));

        // DBに登録されたかを確認する
        Reaction reaction = reactionService.findById(3);
        assertEquals(reaction.getId(), 3);
        assertEquals(reaction.getCount(), 1);
        assertEquals(reaction.getEmoji(), "💪");
        assertEquals(reaction.getReport().getId(), 1);

        // もう一度同じユーザーでリアクションを更新する（countが+1される）
        reactionService.update(3, employeeService.findByCode("1"));

        // DBに登録されたかを確認する
        Reaction reaction2 = reactionService.findById(3);
        assertEquals(reaction2.getId(), 3);
        assertEquals(reaction2.getCount(), 2);
        assertEquals(reaction2.getEmoji(), "💪");
        assertEquals(reaction2.getReport().getId(), 1);
    }

    @Test
    void testDelete() {
        // reaction_idが11のリアクションを削除する
        reactionService.delete(11);

        // DB内にreaction_idが11のリアクションがないことを確認する
        Reaction reaction = reactionService.findById(11);
        assertEquals(reaction, null);
    }

    @Test
    void testDeleteAll() {
        // ID1の日報に対応するリアクションを全て消す
        reactionService.deleteAll(2);

        // DB内にreport_idが1のリアクションがないことを確認する
        List<Reaction> reactionEmpty = reactionService.findByReport(2);
        assertEquals(reactionEmpty.size(), 0);
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
