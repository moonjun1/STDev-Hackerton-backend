package com.stdev.team10.domain.point.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "구매한_원소_내역")
public class PointUserElementStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointUserElementStatusId;

    @Column
    private Long userId;

    @Column(length = 255)
    private String molecularFormula;
}
