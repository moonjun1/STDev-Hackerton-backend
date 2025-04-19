package com.stdev.team10.domain.chemical.controller;

import com.stdev.team10.domain.chemical.service.ChemicalAttackPowerService;

import com.stdev.team10.domain.chemical.service.UserChemicalService;
import com.stdev.team10.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chemical")
@Tag(name = "화학물질 api입니다", description = "화학물질 관련 api입니다.")
public class ChemicalController {


    @Autowired
    private UserChemicalService userChemicalService;

    @Autowired
    private ChemicalAttackPowerService chemicalAttackPowerService;


    @Operation(summary = "사용자가 찾은 모든 화학식 조회", description = "사용자 ID로 해당 사용자가 성공적으로 발견한 모든 화학식을 조회합니다.")
    @GetMapping("/user/formulas/{userId}")
    public ResponseEntity<?> getUserFoundFormulas(@PathVariable Long userId) {
        return userChemicalService.getUserFoundFormulas(userId);
    }

    @Operation(summary = "모든 화학물질에 공격력 부여", description = "ChatGPT API를 사용하여 모든 화학물질에 공격력(1~100)을 부여합니다.")
    @PostMapping("/admin/assign-attack-power")
    public ResponseEntity<?> assignAttackPowerToAllChemicals() {
        try {
            chemicalAttackPowerService.assignAttackPowerToAllChemicals();
            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK, "모든 화학물질에 공격력 부여 완료", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "공격력 부여 실패: " + e.getMessage(), null));
        }
    }

    /**
     * 모든 화학물질에 랜덤 공격력 값 부여 (테스트용)
     */
    @Operation(summary = "모든 화학물질에 랜덤 공격력 부여 (테스트용)", description = "모든 화학물질에 1~100 사이의 랜덤 공격력 값을 부여합니다.")
    @PostMapping("/admin/assign-random-attack-power")
    public ResponseEntity<?> assignRandomAttackPower() {
        try {
            chemicalAttackPowerService.assignRandomAttackPower();
            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK, "랜덤 공격력 부여 완료", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "랜덤 공격력 부여 실패: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "화학식으로 공격력 조회", description = "분자식(molecularFormula)을 통해 화학물질의 공격력을 조회합니다.")
    @GetMapping("/attack-power/{molecularFormula}")
    public ResponseEntity<?> getChemicalAttackPower(@PathVariable String molecularFormula) {
        return chemicalAttackPowerService.getChemicalAttackPower(molecularFormula);
    }
}