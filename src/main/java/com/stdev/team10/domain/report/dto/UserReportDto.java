package com.stdev.team10.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserReportDto {
    private Long userId;
    private String username;
    private String organization;
    private LocalDateTime generatedAt;
    private String reportContent; // Markdown 형식의 보고서 내용
}