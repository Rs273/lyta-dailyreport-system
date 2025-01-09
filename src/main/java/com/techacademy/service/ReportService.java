package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

     // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        // 日付重複チェック
        ErrorKinds result = isDateCheckError(report);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {
        Report reportInDB = findById(report.getId());

        // 日付重複チェック
        ErrorKinds result = isDateCheckError(report);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        report.setDeleteFlg(reportInDB.isDeleteFlg());
        report.setCreatedAt(reportInDB.getCreatedAt());

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 従業員一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }


    // 日付重複チェック
    private ErrorKinds isDateCheckError(Report report) {

        List<Report> reports = findAll();

        for(Report res : reports) {
            if(res.getEmployee().getCode().equals(report.getEmployee().getCode())) {
                if(res.getReportDate().equals(report.getReportDate())){
                    if(!res.getId().equals(report.getId())) {
                        return ErrorKinds.DATECHECK_ERROR;
                    }
                }
            }
        }

        return ErrorKinds.CHECK_OK;
    }
}
