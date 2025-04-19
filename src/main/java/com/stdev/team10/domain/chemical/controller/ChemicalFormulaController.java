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
     * @return 검색 결과
     */
    @Tag(name = "화학식 검색하는 API", description = "사용자 닉네임 있어야함")
    @PostMapping("/formula/search/{userName}")
    public ResponseEntity<?> searchChemicalByFormula(
            @RequestBody ChemicalFormulaDto formulaDto) {
        return chemicalFormulaService.searchChemicalByFormula(formulaDto);
    }

}