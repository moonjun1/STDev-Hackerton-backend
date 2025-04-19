package com.stdev.team10.domain.title.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TitleDto {
    private Long titleId;
    private String titleName;
    private String description;
    private String unlockCondition;
    private Boolean isUnlocked; // 사용자가 해당 칭호를 획득했는지 여부
    private Boolean isActive; // 현재 사용중인 칭호인지 여부
}