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


}
