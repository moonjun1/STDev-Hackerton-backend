package com.stdev.team10.domain.user.dto;

import com.stdev.team10.domain.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long userId;
    private String username;
    private String organization;

    public UserEntity toEntity() {
        return UserEntity.builder()
                .userId(this.userId)
                .username(this.username)
                .organization(this.organization)
                .build();
    }
}