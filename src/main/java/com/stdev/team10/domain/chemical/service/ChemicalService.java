package com.stdev.team10.domain.chemical.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stdev.team10.domain.chemical.dto.ChemicalDto;
import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import com.stdev.team10.domain.chemical.repository.ChemicalRepository;
import com.stdev.team10.global.common.data.ChemicalData;
import com.stdev.team10.global.common.data.ChemicalData2;
import com.stdev.team10.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class ChemicalService {
    @Autowired
    private ChemicalRepository chemicalRepository;

    public ResponseEntity<?> chemicalFindAll(){
        List<ChemicalEntity> chemicalEntityList = chemicalRepository.findAll();
        if(chemicalEntityList.isEmpty()) {
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질이 없습니다.", null));
        } else {
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질 조회 성공", chemicalEntityList));
        }
    }

    public ResponseEntity chemicalFindByMolecularFormula(String molecularFormula) {
        if(!chemicalRepository.existsByMolecularFormulaIgnoreCase(molecularFormula)) {
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질이 없습니다.", null));
        } else {
            ChemicalEntity chemicalEntity = chemicalRepository.findByMolecularFormulaIgnoreCase(molecularFormula);
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질 조회 성공", chemicalEntity));
        }
    }

}
