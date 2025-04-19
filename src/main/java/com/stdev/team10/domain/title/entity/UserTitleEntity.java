package com.stdev.team10.domain.title.entity;

import com.stdev.team10.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "사용자_칭호")
public class UserTitleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "title_id")
    private TitleEntity title;

    @Column(nullable = false)
    private LocalDateTime acquiredAt; // 칭호 획득 시간

    @Column(nullable = false)
    private Boolean isActive; // 현재 사용 중인 칭호인지 여부
}