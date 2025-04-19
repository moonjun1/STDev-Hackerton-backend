package com.stdev.team10.domain.title.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "칭호")
public class TitleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long titleId;

    @Column(length = 100, nullable = false, unique = true)
    private String titleName; // 칭호 이름 (예: "초등교육완성")

    @Column(length = 500)
    private String description; // 칭호에 대한 설명

    @Column(length = 100)
    private String unlockCondition; // 획득 조건 설명 (UI 표시용)
}