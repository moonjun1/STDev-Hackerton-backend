package com.stdev.team10.domain.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class RankingDtos {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class UserRankingDto {
        private int rank;
        private Long userId;
        private String username;
        private String groupName;
        private long successCount;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class OrganizationRankingDto {
        private int rank;
        private String groupName;
        private long successCount;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class RankingResponseDto {
        private List<UserRankingDto> userRankings;
        private List<OrganizationRankingDto> organizationRankings;
    }
}