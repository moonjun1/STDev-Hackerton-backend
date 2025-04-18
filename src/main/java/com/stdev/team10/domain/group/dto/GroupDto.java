package com.stdev.team10.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class GroupDto {

    @Schema(hidden = true)
    private Long groupId;

    @Schema(hidden = true)
    private String groupName;
}
