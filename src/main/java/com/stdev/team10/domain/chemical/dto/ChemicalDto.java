package com.stdev.team10.domain.chemical.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChemicalDto {
    private Long chemicalId;

    private String chemicalNameKo;

    private String chemicalNameEn;

    private String molecularFormula;

    private String chemicalDescriptionKo;

    private String educationLevel;

    public ChemicalEntity toEntity() {
        return ChemicalEntity.builder()
                .chemicalId(this.chemicalId)
                .chemicalNameKo(this.chemicalNameKo)
                .chemicalNameEn(this.chemicalNameEn)
                .molecularFormula(this.molecularFormula)
                .chemicalDescriptionKo(this.chemicalDescriptionKo)
                .educationLevel(this.educationLevel)
                .build();
    }
}
