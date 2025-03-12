package com.techacademy.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Following;
import com.techacademy.entity.Report;
import com.techacademy.service.FollowingService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("followings")
public class FollowingController {

    private final FollowingService followingService;
    private final ReportService reportService;

    @Autowired
    public FollowingController(FollowingService followingService, ReportService reportService) {
        this.followingService = followingService;
        this.reportService = reportService;
    }

    @GetMapping(value = "/report_list")
    public String reportList(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        // フォローしている従業員のコードリストを取得
        List<String> codeList = new ArrayList<String>();
        List<Following> followingList = followingService.findByFollower(userDetail.getUsername());
        for(Following following: followingList) {
            codeList.add(following.getFollowingEmployee().getCode());
        }

        List<Report> reportList = reportService.findByEmployeeCodeList(codeList);

        model.addAttribute("listSize", reportList.size());
        model.addAttribute("reportList", reportList);

        return "followings/report_list";
    }
}
