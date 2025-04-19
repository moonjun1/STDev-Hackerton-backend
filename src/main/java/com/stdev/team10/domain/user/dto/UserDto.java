package com.stdev.team10.domain.user.dto;

import com.stdev.team10.domain.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @Schema(hidden = true)
    private Long userId;

    @Schema(example = "홍길동")
    private String userName;

    @Schema(example = "상경중학교")
    private String groupName;

    @Schema(hidden = true)
    private Long userPoint;

    public UserEntity toEntity() {
        return UserEntity.builder()
                .userName(this.userName)
                .groupName(this.groupName)
                .userPoint(this.userPoint)
                .build();
    }
}