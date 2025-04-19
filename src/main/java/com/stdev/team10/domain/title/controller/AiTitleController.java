package com.stdev.team10.domain.title.controller;

import com.stdev.team10.domain.title.dto.AiTitleDto;
import com.stdev.team10.domain.title.service.AiTitleService;
import com.stdev.team10.domain.title.service.TitleService;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/title/ai")
@Tag(name = "AI 칭호", description = "AI가 생성한 맞춤형 칭호 기능")
public class AiTitleController {

    @Autowired
    private AiTitleService aiTitleService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private UserRepository userRepository;

    /**
     * AI가 생성한 맞춤형 칭호 목록 조회
     */
    @Operation(summary = "ai 칭호를 반환", description = "사용자가 지금까지 조합한 화합을을 토대로 칭호를 생성합니다")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAiGeneratedTitles(@PathVariable Long userId) {
        try {
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.response(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", null));
            }

            UserEntity user = userOptional.get();
            List<AiTitleDto> aiTitles = aiTitleService.generateAiTitles(user);

            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK, "AI 칭호 생성 성공", aiTitles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "AI 칭호 생성 실패: " + e.getMessage(), null));
        }
    }

    /**
     * AI가 생성한 칭호를 사용자의 칭호로 저장
     */
    @Operation(summary = "AI가 생성한 칭호를 사용자의 칭호로 저장", description = "사용자의 칭호로 넣고싶을떄 사용하시면 됩니다")
    @PostMapping("/{userId}/save")
    public ResponseEntity<?> saveAiGeneratedTitle(
            @PathVariable Long userId,
            @RequestBody AiTitleDto aiTitleDto) {
        try {
            return titleService.saveAiGeneratedTitle(userId, aiTitleDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "AI 칭호 저장 실패: " + e.getMessage(), null));
        }
    }
}