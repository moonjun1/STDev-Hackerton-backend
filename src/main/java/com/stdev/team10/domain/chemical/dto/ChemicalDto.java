package com.stdev.team10.domain.chemical.dto;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
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

    private Integer attackPower;


    public ChemicalEntity toEntity() {
        return ChemicalEntity.builder()
                .chemicalId(this.chemicalId)
                .chemicalNameKo(this.chemicalNameKo)
                .chemicalNameEn(this.chemicalNameEn)
                .molecularFormula(this.molecularFormula)
                .chemicalDescriptionKo(this.chemicalDescriptionKo)
                .educationLevel(this.educationLevel)
                .attackPower(this.attackPower)
                .build();
    }
}
