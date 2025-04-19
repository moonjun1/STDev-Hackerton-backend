package com.stdev.team10.domain.title.controller;

import com.stdev.team10.domain.title.service.TitleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/title")
@Tag(name = "아직 미구현", description = "미구현")
public class TitleController {

    @Autowired
    private TitleService titleService;

    /**
     * 사용자의 모든 칭호 목록 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllTitles(@PathVariable Long userId) {
        return titleService.getAllTitles(userId);
    }

    /**
     * 특정 칭호 활성화
     */
    @PostMapping("/{userId}/activate/{titleId}")
    public ResponseEntity<?> activateTitle(
            @PathVariable Long userId,
            @PathVariable Long titleId) {
        return titleService.activateTitle(userId, titleId);
    }
}