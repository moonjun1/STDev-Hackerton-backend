package com.stdev.team10.domain.report.controller;

import com.stdev.team10.domain.report.dto.UserReportDto;
import com.stdev.team10.domain.report.service.UserReportService;
import com.stdev.team10.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Tag(name = "사용자 보고서", description = "사용자의 화학 활동 분석 보고서")
public class UserReportController {

    @Autowired
    private UserReportService userReportService;

    /**
     * 사용자의 화학 활동 분석 보고서 생성
     */
    @Operation(summary = "사용자의 화학 활동 분석 보고서 생성", description = "유저 id만 넣으면 됩니다")
    @GetMapping("/{userId}")
    public ResponseEntity<?> generateUserReport(@PathVariable Long userId) {
        try {
            UserReportDto report = userReportService.generateUserReport(userId);
            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK, "사용자 보고서 생성 성공", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "사용자 보고서 생성 실패: " + e.getMessage(), null));
        }
    }
}