package com.stdev.team10.domain.chemical.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChemicalFormulaDto {
    private String formula; // 사용자가 입력하는 화학식 (예: "h + h + o")
}