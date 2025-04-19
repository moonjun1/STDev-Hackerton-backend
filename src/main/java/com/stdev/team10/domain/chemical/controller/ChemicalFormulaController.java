package com.stdev.team10.domain.chemical.controller;

import com.stdev.team10.domain.chemical.dto.ChemicalFormulaDto;
import com.stdev.team10.domain.chemical.service.ChemicalFormulaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chemical")
public class ChemicalFormulaController {

    @Autowired
    private ChemicalFormulaService chemicalFormulaService;

    /**
     * 화학식을 검색하는 API (사용자 ID 제공, 랭킹에 포함)
     * @param formulaDto 화학식 정보
     * @param userId 사용자 ID
     * @return 검색 결과
     */
    @Tag(name = "화학식 검색하는 API", description = "사용자 id 있어야함")
    @PostMapping("/formula/search")
    public ResponseEntity<?> searchChemicalByFormula(
            @RequestBody ChemicalFormulaDto formulaDto,
            @RequestParam Long userId) {
        return chemicalFormulaService.searchChemicalByFormula(formulaDto, userId);
    }

    /**
     * 화학식을 검색하는 API (랭킹에 포함되지 않음)
     * @param formulaDto 화학식 정보
     * @return 검색 결과
     */
    @Tag(name = "유저 id 없이 화학식 검색하는 API", description = "사용자 id 필요없음 그냥 id 없이 확인하고 싶은데 쓰는 api")
    @PostMapping("/formula/search/anonymous")
    public ResponseEntity<?> searchChemicalByFormulaAnonymous(
            @RequestBody ChemicalFormulaDto formulaDto) {
        return chemicalFormulaService.searchChemicalByFormula(formulaDto);
    }
}