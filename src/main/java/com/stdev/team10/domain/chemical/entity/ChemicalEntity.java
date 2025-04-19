package com.stdev.team10.domain.chemical.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "물질")
public class ChemicalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 설정
    private Long chemicalId;

    @Column(length = 255)
    private String chemicalNameKo;

    @Column(length = 255)
    private String chemicalNameEn;

    @Column(length = 255)
    private String molecularFormula;

    @Column(length = 1000)
    private String chemicalDescriptionKo;

    // "초등", "중등", "고등", "기타" 값 가질수 있음
    @Column(length = 100)
    private String educationLevel;

    @Column
    private Long power;
}
