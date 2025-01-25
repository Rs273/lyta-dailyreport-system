package com.techacademy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Giver;
import com.techacademy.entity.Reaction;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReactionRepository;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final GiverService giverService;

    public ReactionService(ReactionRepository reactionRepository, GiverService giverService) {
        this.reactionRepository = reactionRepository;
        this.giverService = giverService;
    }

    // リアクション保存
    @Transactional
    public void save(String emoji, Report report) {
        Reaction reaction = new Reaction();

        reaction.setCount(0);
        reaction.setReport(report);
        reaction.setEmoji(emoji);
        reactionRepository.save(reaction);

        return;
    }

    // 日報に対応するリアクション全て保存
    @Transactional
    public void saveAll(Report report) {

        save("👍", report);
        save("✅", report);
        save("💪", report);
        save("👀", report);
        save("🙌", report);

        return;
    }

    // リアクション数更新
    @Transactional
    public void update(Integer id, Employee employee) {

        // リアクションに対応するgiverListを取得
        List<Giver> giverList = giverService.findByReaction(id);

        // リアクション数に加算する数
        int addition = 1;

        // リアクションが0の場合
        if(giverList.size() == 0) {
            giverService.save(employee, findById(id));
        }

        // リアクションが1以上の場合、
        // すでにリアクションをつけている人とログイン中のユーザーが一致した場合、リアクションを消す(additionを-1にしてgiverを物理削除)
        // 一致しない場合、リアクションをつける(additionを1にして、giverを保存)
        for(Giver giver : giverList) {
            if(giver.getEmployee().getCode().equals(employee.getCode())) {
                addition = -1;
                giverService.delete(giver.getId());
            }else {
                giverService.save(employee, findById(id));
            }
        }

        Reaction reactionInDb = findById(id);

        Reaction reaction = new Reaction();
        reaction.setId(id);
        reaction.setEmoji(reactionInDb.getEmoji());
        reaction.setCount(reactionInDb.getCount() + addition);
        reaction.setReport(reactionInDb.getReport());

        reactionRepository.save(reaction);

        return;
    }

    // リアクション削除
    @Transactional
    public void delete(Integer id) {
        Reaction reaction = findById(id);
        reactionRepository.delete(reaction);

        // リアクションに紐づくリアクション付与者情報を取得して削除
        List<Giver> giverList = giverService.findByReaction(id);

        for(Giver giver: giverList) {
            giverService.delete(giver.getId());
        }

        return;
    }

    // 日報に対応するリアクションを全て削除
    @Transactional
    public void deleteAll(Integer reportId) {
        List<Reaction> reactionList = findByReport(reportId);

        for(Reaction reaction : reactionList) {
            delete(reaction.getId());
        }
        return;
    }

    // 全件表示
    public List<Reaction> findAll() {
        return reactionRepository.findAll();
    }

    // 1件検索
    public Reaction findById(Integer id) {
        // findByIdで検索
        Optional<Reaction> option = reactionRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Reaction report = option.orElse(null);
        return report;
    }

    // 指定された日報のリアクションリストを返す
    public List<Reaction> findByReport(Integer reportId){
        List<Reaction> reactions = findAll();
        List<Reaction> result = new ArrayList<Reaction>();

        for(Reaction reaction : reactions) {
            if(reaction.getReport().getId().equals(reportId)) {
                result.add(reaction);
            }
        }

        return result;
    }
}
