package com.stdev.team10.domain.title.controller;

import com.stdev.team10.domain.title.service.TitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/title")
@Tag(name = "사용자의 칭호를 조회하는곳", description = "칭호조회")
public class TitleController {

    @Autowired
    private TitleService titleService;

    /**
     * 사용자의 모든 칭호 목록 조회
     */
    @Operation(summary = "사용자의 모든 칭호 목록 조회", description = "특정 사용자의 모든 칭호 목록을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllTitles(@PathVariable Long userId) {
        return titleService.getAllTitles(userId);
    }

    /**
     * 특정 칭호 활성화
     */
    @Operation(summary = "특정 칭호 활성화(이건 안써도 될듯해요)", description = "특정 사용자의 특정 칭호를 활성화합니다.")
    @PostMapping("/{userId}/activate/{titleId}")
    public ResponseEntity<?> activateTitle(
            @PathVariable Long userId,
            @PathVariable Long titleId) {
        return titleService.activateTitle(userId, titleId);
    }
}