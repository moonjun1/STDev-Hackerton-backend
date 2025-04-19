package com.stdev.team10.domain.ranking.entity;

import com.stdev.team10.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "검색_이력")
public class FormulaSearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(length = 255)
    private String matchedFormula; // 실제 매칭된 완성 화학식

    @Column
    private Boolean successful; // 검색 성공 여부
}