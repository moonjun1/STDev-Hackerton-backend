package com.stdev.team10.domain.chemical.controller;

import com.stdev.team10.domain.chemical.service.ChemicalService;
import com.stdev.team10.domain.chemical.service.UserChemicalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chemical")
@Tag(name = "화학물질 api입니다", description = "화학물질 관련 api입니다.")
public class ChemicalController {

    @Autowired
    private ChemicalService chemicalService;

    @Autowired
    private UserChemicalService userChemicalService;

    @Operation(summary = "사용자가 찾은 모든 화학식 조회", description = "사용자 ID로 해당 사용자가 성공적으로 발견한 모든 화학식을 조회합니다.")
    @GetMapping("/user/formulas/{userId}")
    public ResponseEntity<?> getUserFoundFormulas(@PathVariable Long userId) {
        return userChemicalService.getUserFoundFormulas(userId);
    }
}