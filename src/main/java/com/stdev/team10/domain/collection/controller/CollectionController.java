package com.stdev.team10.domain.collection.controller;

import com.stdev.team10.domain.collection.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collection")
@Tag(name = "화학물질 도감 api 입니다", description = "도감에서 사용될 api 모음 입니다.")
public class CollectionController {

    @Autowired
    CollectionService collectionService;

    @Operation(summary = "물질 전부 조회 api", description = "모든 물질 정보를 조회합니다.")
    @GetMapping("/find/all")
    public ResponseEntity<?> chemicalFindAll() {
        return collectionService.chemicalFindAll();
    }

    @Operation(summary = "화학식으로 특정 물질 정보 조회 api", description = "화학식으로 특정 물질 정ㅎ보를 도감에서 조회합니다.")
    @GetMapping("/find/{molecularFormula}")
    public ResponseEntity<?> chemicalFindByMolecularFormula(@PathVariable String molecularFormula) {
        return collectionService.chemicalFindByMolecularFormula(molecularFormula);
    }
}

