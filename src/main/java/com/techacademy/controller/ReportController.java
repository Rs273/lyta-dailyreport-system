package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Giver;
import com.techacademy.entity.Reaction;
import com.techacademy.entity.Report;
import com.techacademy.service.GiverService;
import com.techacademy.service.ReactionService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final ReactionService reactionService;
    private final GiverService giverService;

    @Autowired
    public ReportController(ReportService reportService, ReactionService reactionService, GiverService giverService) {
        this.reportService = reportService;
        this.reactionService = reactionService;
        this.giverService = giverService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        if(userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
        }else if(userDetail.getEmployee().getRole() == Employee.Role.GENERAL) {
            model.addAttribute("listSize", reportService.findByEmployee(userDetail.getEmployee()).size());
            model.addAttribute("reportList", reportService.findByEmployee(userDetail.getEmployee()));
        }

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));

        List<Reaction> reactionList = reactionService.findByReport(id);
        model.addAttribute("reactionList", reactionList);

        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        report.setEmployee(userDetail.getEmployee());
        model.addAttribute("report", report);

        return "reports/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }

        report.setEmployee(userDetail.getEmployee());

        ErrorKinds result = reportService.save(report);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail, model);
        }

        // 関連するリアクションを作成
        reactionService.saveAll(report);

        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable("id") Integer id, Report report, Model model) {
        if(id != null) {
            // Modelに登録
            model.addAttribute("report", reportService.findById(id));
        } else {
            model.addAttribute("report", report);
        }
        // 更新画面に遷移
        return "reports/update";
    }


    // 日報更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@Validated Report report, BindingResult res, Model model) {
        // 従業員情報を日報情報に格納する
        report.setEmployee(reportService.findById(report.getId()).getEmployee());

        // 入力チェック
        if (res.hasErrors()) {
            return edit(null, report, model);
        }

        // 従業員情報を更新する
        ErrorKinds result = reportService.update(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return edit(null, report, model);
        }

        // 一覧画面にリダイレクト
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {

        reportService.delete(id);

        // 関連するリアクションを物理削除
        reactionService.deleteAll(id);

        return "redirect:/reports";
    }

    // リアクション処理
    @PostMapping(value = "/{id}/reaction")
    public String reaction(@PathVariable("id") Integer id, @RequestParam("id") String reportId, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // リアクションに対応するgiverListを取得
        List<Giver> giverList = giverService.findByReaction(id);

        // リアクション数に加算する数
        int addition = 1;

        // リアクションが0の場合
        if(giverList.size() == 0) {
            giverService.save(userDetail.getUsername(), id);
        }

        // リアクションが1以上の場合、
        // すでにリアクションをつけている人とログイン中のユーザーが一致した場合、リアクションを消す(additionを-1にしてgiverを物理削除)
        // 一致しない場合、リアクションをつける(additionを1にして、giverを保存)
        for(Giver giver : giverList) {
            if(giver.getEmployee().getCode().equals(userDetail.getUsername())) {
                addition = -1;
                giverService.delete(giver.getId());
            }else {
                giverService.save(userDetail.getUsername(), id);
            }
        }

        reactionService.update(id, addition);

        return "redirect:/reports/" + reportId + "/";
    }
}
