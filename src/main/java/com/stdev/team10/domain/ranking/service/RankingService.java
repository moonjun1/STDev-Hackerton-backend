package com.stdev.team10.domain.ranking.service;

import com.stdev.team10.domain.ranking.dto.RankingDtos;
import com.stdev.team10.domain.ranking.repository.SearchHistoryRepository;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.global.common.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankingService {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    /**
     * 개인 및 조직 랭킹을 조회하는 메서드
     * @return 개인 및 조직 랭킹 정보
     */
    public ResponseEntity<?> getRankings() {
        try {
            List<RankingDtos.UserRankingDto> userRankings = getTopUserRankings(10); // 상위 10명
            List<RankingDtos.OrganizationRankingDto> organizationRankings = getTopOrganizationRankings(10); // 상위 10개 조직

            RankingDtos.RankingResponseDto responseDto = RankingDtos.RankingResponseDto.builder()
                    .userRankings(userRankings)
                    .organizationRankings(organizationRankings)
                    .build();

            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "랭킹 조회 성공", responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "랭킹 조회 실패: " + e.getMessage(), null));
        }
    }

    /**
     * 상위 N명의 사용자 랭킹을 조회하는 메서드
     * @param limit 조회할 사용자 수
     * @return 상위 N명의 사용자 랭킹 목록
     */
    private List<RankingDtos.UserRankingDto> getTopUserRankings(int limit) {
        // 중복 화학식 제외한 검색 성공 횟수 조회
        List<Object[]> results = searchHistoryRepository.findUsersByDistinctSuccessfulSearches();
        List<RankingDtos.UserRankingDto> userRankings = new ArrayList<>();

        int rank = 1;
        for (Object[] result : results) {
            if (rank > limit) break;

            UserEntity user = (UserEntity) result[0];
            long count = (long) result[1];

            RankingDtos.UserRankingDto rankingDto = RankingDtos.UserRankingDto.builder()
                    .rank(rank++)
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .organization(user.getOrganization())
                    .successCount(count) // 중복 제외된 성공 카운트
                    .build();

            userRankings.add(rankingDto);
        }

        return userRankings;
    }

    /**
     * 상위 N개의 조직 랭킹을 조회하는 메서드
     * @param limit 조회할 조직 수
     * @return 상위 N개의 조직 랭킹 목록
     */
    private List<RankingDtos.OrganizationRankingDto> getTopOrganizationRankings(int limit) {
        // 중복 화학식 제외한 조직별 검색 성공 횟수 조회
        List<Object[]> results = searchHistoryRepository.countDistinctSuccessfulSearchesByOrganization();
        List<RankingDtos.OrganizationRankingDto> organizationRankings = new ArrayList<>();

        int rank = 1;
        for (Object[] result : results) {
            if (rank > limit) break;

            String organization = (String) result[0];
            long count = (long) result[1];

            if (organization != null && !organization.isEmpty()) {
                RankingDtos.OrganizationRankingDto rankingDto = RankingDtos.OrganizationRankingDto.builder()
                        .rank(rank++)
                        .organization(organization)
                        .successCount(count) // 중복 제외된 성공 카운트
                        .build();

                organizationRankings.add(rankingDto);
            }
        }

        return organizationRankings;
    }
}