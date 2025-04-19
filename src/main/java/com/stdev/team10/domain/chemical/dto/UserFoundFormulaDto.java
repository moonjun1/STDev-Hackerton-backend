package com.stdev.team10.domain.chemical.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserFoundFormulaDto {
    private String molecularFormula;
    private String chemicalNameKo;
    private String chemicalNameEn;
    private String educationLevel;
    private String chemicalDescriptionKo;
}