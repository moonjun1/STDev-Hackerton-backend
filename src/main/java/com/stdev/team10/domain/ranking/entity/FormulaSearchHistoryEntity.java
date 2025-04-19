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
@Table(name = "화학식_검색_이력")
public class FormulaSearchHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private Boolean successful; // 검색 성공 여부만 기록
}