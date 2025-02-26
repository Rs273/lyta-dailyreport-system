package com.techacademy.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
import org.springframework.web.multipart.MultipartFile;

import com.techacademy.FileDownloader;
import com.techacademy.ImageFileOperator;
import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Comment;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Reaction;
import com.techacademy.entity.Report;
import com.techacademy.service.CommentService;
import com.techacademy.service.ReactionService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final ReactionService reactionService;
    private final CommentService commentService;
    private final ImageFileOperator imageFileOperator;
    private final FileDownloader fileDownloader;

    @Autowired
    public ReportController(ReportService reportService, ReactionService reactionService, CommentService commentService, ImageFileOperator imageFileOperator, FileDownloader fileDownloader) {
        this.reportService = reportService;
        this.reactionService = reactionService;
        this.commentService = commentService;
        this.imageFileOperator = imageFileOperator;
        this.fileDownloader = fileDownloader;
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

        // maxOfIdが-1の時、maxOfIdを更新する
        if(ReportService.maxOfId < 0) {
            ReportService.maxOfId = reportService.getMaxOfId();
        }

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

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res,@RequestParam("imageFile") MultipartFile imageFile, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }

        report.setEmployee(userDetail.getEmployee());

        Integer reportId = ReportService.maxOfId + 1;
        // ファイルが選択されている場合
        if(!imageFile.isEmpty()) {
            // 画像ファイルをセーブする
            ErrorKinds result = imageFileOperator.save(reportId, imageFile);
            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, userDetail, model);
            }

            // 日報情報に設定する
            report.setImageFileName(imageFile.getOriginalFilename());
            if(imageFile.getContentType().equals("application/pdf")) {
                String basename = imageFile.getOriginalFilename();
                String filename = basename.substring(0,basename.lastIndexOf('.'));
                report.setImageFilePath(ImageFileOperator.CONVERT_DIR_HTML + File.separator + reportId.toString() + File.separator + filename + ".jpg");
            } else {
                report.setImageFilePath(ImageFileOperator.DIR_HTML + File.separator + reportId.toString() + File.separator + imageFile.getOriginalFilename());
            }
        }

        ErrorKinds result = reportService.save(report);
        if (ErrorMessage.contains(result)) {
            imageFileOperator.deleteWithCovertedFile(reportId, imageFile.getOriginalFilename());
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
    public String update(@Validated Report report, BindingResult res,@RequestParam("imageFile") MultipartFile imageFile, Model model) {
        // 従業員情報を日報情報に格納する
        report.setEmployee(reportService.findById(report.getId()).getEmployee());

        // 入力チェック
        if (res.hasErrors()) {
            return edit(null, report, model);
        }

        // ファイルが選択されている場合
        if(!imageFile.isEmpty()) {
            // 画像ファイルをセーブする
            ErrorKinds result = imageFileOperator.save(report.getId(), imageFile);
            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return edit(null, report, model);
            }

            // 日報情報に設定する
            report.setImageFileName(imageFile.getOriginalFilename());
            if(imageFile.getContentType().equals("application/pdf")) {
                String basename = imageFile.getOriginalFilename();
                String filename = basename.substring(0,basename.lastIndexOf('.'));
                report.setImageFilePath(ImageFileOperator.CONVERT_DIR_HTML + File.separator + report.getId().toString() + File.separator + filename + ".jpg");
            } else {
                report.setImageFilePath(ImageFileOperator.DIR_HTML + File.separator + report.getId().toString() + File.separator + imageFile.getOriginalFilename());
            }
        }

        // 従業員情報を更新する
        ErrorKinds result = reportService.update(report);

        if (ErrorMessage.contains(result)) {
            imageFileOperator.deleteWithCovertedFile(report.getId(), imageFile.getOriginalFilename());
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

        // 必要な情報をcommentに格納する
        comment.setEmployee(userDetail.getEmployee());
        comment.setReport(reportService.findById(reportId));

        // コメントを保存する
        commentService.save(comment);

        return "redirect:/reports/" + reportId + "/";
    }

    // コメント更新処理
    @PostMapping(value = "/{reportId}/{commentId}/update_comment")
    public String updateComment(@RequestParam("commentContent") String commentContent, @PathVariable("reportId") Integer reportId, @PathVariable("commentId") Integer commentId, Comment comment, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        // 更新するコメントの内容をcommentToUpdateに格納
        Comment commentToUpdate = new Comment();
        commentToUpdate.setId(commentId);
        commentToUpdate.setContent(commentContent);

        // コメント情報を更新する
        ErrorKinds result = commentService.update(commentToUpdate);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return detail(reportId, comment, userDetail, model);
        }

        return "redirect:/reports/" + reportId + "/";
    }

    // コメント編集切替処理
    @PostMapping(value = "/{reportId}/{commentId}/edit_comment")
    public String editComment(@PathVariable("reportId") Integer reportId, @PathVariable("commentId") Integer commentId, Model model) {

        // コメント情報の編集中フラグを変更する
        commentService.changeEditingFlg(commentId);

        return "redirect:/reports/" + reportId + "/";
    }

    // コメント削除処理
    @PostMapping(value = "/{reportId}/{commentId}/delete_comment")
    public String deleteComment(@PathVariable("reportId") Integer reportId, @PathVariable("commentId") Integer commentId, Model model) {

        // コメント情報を削除する
        commentService.delete(commentId);

        return "redirect:/reports/" + reportId + "/";
    }

    // 画像ファイル削除処理
    @PostMapping(value = "/{reportId}/delete_image")
    public String deleteImage(@PathVariable("reportId") Integer reportId, @RequestParam("imageFileName") String imageFileName) {

        // ファイルを削除
        ErrorKinds result = imageFileOperator.deleteWithCovertedFile(reportId, imageFileName);

        // DBの内容を更新する
        Report report = reportService.findById(reportId);
        report.setImageFileName(null);
        report.setImageFilePath(null);
        reportService.update(report);

        return "redirect:/reports/" + reportId + "/update";
    }

    // csvファイルダウンロード処理
    @PostMapping(value = "/{reportId}/download")
    public String download(@PathVariable("reportId") Integer reportId, HttpServletResponse response){

        Report report = reportService.findById(reportId);
        List<Reaction> reactionList = reactionService.findByReport(reportId);
        List<Comment> commentList = commentService.findByReport(reportId);

        fileDownloader.download(report, reactionList, commentList, response);

        return "redirect:/reports/" + reportId + "/";
    }

}
