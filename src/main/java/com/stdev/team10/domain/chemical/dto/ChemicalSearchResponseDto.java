package com.STDev.team10.domain.chemical.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChemicalSearchResponseDto {
    private boolean success;
    private String message;
    private ChemicalDto chemical; // 검색된 화학 물질 정보
    private String originalInput; // 사용자 원래 입력
    private String convertedFormula; // 변환된 화학식
}