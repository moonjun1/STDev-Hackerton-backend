package com.stdev.team10.domain.ranking.controller;

import com.stdev.team10.domain.ranking.service.RankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ranking")
@Tag(name = "랭킹", description = "유저랭킹 , 조직 랭킹 순으로 나옴")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    /**
     * 상위 10명의 개인 랭킹과 상위 10개 조직 랭킹을 조회하는 API
     * @return 개인 및 조직 랭킹 정보
     */
    @GetMapping
    public ResponseEntity<?> getRankings() {
        return rankingService.getRankings();
    }
}