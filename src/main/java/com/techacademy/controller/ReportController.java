package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.techacademy.entity.Comment;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Giver;
import com.techacademy.entity.Reaction;
import com.techacademy.entity.Report;
import com.techacademy.service.CommentService;
import com.techacademy.service.ReactionService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final ReactionService reactionService;
    private final CommentService commentService;

    @Autowired
    public ReportController(ReportService reportService, ReactionService reactionService, CommentService commentService) {
        this.reportService = reportService;
        this.reactionService = reactionService;
        this.commentService = commentService;
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

        // 全てのコメントの編集中フラグをfalseにする
        commentService.setFalseToEditingFlg();

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, @ModelAttribute Comment comment, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        model.addAttribute("report", reportService.findById(id));
        model.addAttribute("userDetailCode", userDetail.getUsername());

        // 対応するリアクション一覧を取得する
        List<Reaction> reactionList = reactionService.findByReport(id);
        model.addAttribute("reactionList", reactionList);

        // 対応するコメント一覧を取得する
        List<Comment> commentList = commentService.findByReport(id);
        model.addAttribute("commentList", commentList);

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

        return "redirect:/reports";
    }

    // リアクション処理
    @PostMapping(value = "/{id}/reaction")
    public String reaction(@PathVariable("id") Integer id, @RequestParam("id") String reportId, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        reactionService.update(id, userDetail.getEmployee());

        return "redirect:/reports/" + reportId + "/";
    }

    // コメント登録処理
    @PostMapping(value = "/{reportId}/add_comment")
    public String addComment(@Validated Comment comment, BindingResult res, @PathVariable("reportId") Integer reportId, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return detail(reportId, comment, userDetail, model);
        }

        // 必要な情報をcommentに格納
        comment.setEmployee(userDetail.getEmployee());
        comment.setReport(reportService.findById(reportId));

        // コメントを保存
        commentService.save(comment);

        return "redirect:/reports/" + reportId + "/";
    }

 // コメント編集切替処理
    @PostMapping(value = "/{reportId}/{commentId}/update_comment")
    public String updateComment(@RequestParam("commentContent")String commentContent, @PathVariable("reportId") Integer reportId, @PathVariable("commentId") Integer commentId, Model model) {

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent(commentContent);

        commentService.update(comment);

        return "redirect:/reports/" + reportId + "/";
    }

    // コメント編集切替処理
    @PostMapping(value = "/{reportId}/{commentId}/edit_comment")
    public String editComment(@PathVariable("reportId") Integer reportId, @PathVariable("commentId") Integer commentId, Model model) {

        commentService.changeEditingFlg(commentId);

        return "redirect:/reports/" + reportId + "/";
    }

    // コメント削除処理
    @PostMapping(value = "/{reportId}/{commentId}/delete_comment")
    public String deleteComment(@PathVariable("reportId") Integer reportId, @PathVariable("commentId") Integer commentId, Model model) {

        commentService.delete(commentId);

        return "redirect:/reports/" + reportId + "/";
    }
}
