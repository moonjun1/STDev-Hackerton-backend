package com.stdev.team10.domain.chemical.controller;

import com.stdev.team10.domain.chemical.service.ChemicalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
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

    @Operation(summary = "물질 전부 조회 api", description = "모든 물질 정보를 조회합니다.")
    @GetMapping("/find/all")
    public ResponseEntity<?> chemicalFindAll() {
        return chemicalService.chemicalFindAll();
    }

    @Operation(summary = "화학식으로 특정 물질 정보 조회 api", description = "화학식으로 특정 물질 정보를 조회합니다.")
    @GetMapping("/find/{molecularFormula}")
    public ResponseEntity<?> chemicalFindByMolecularFormula(@PathVariable String molecularFormula) {
        return chemicalService.chemicalFindByMolecularFormula(molecularFormula);
    }
}
