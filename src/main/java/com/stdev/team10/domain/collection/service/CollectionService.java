package com.stdev.team10.domain.collection.service;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import com.stdev.team10.domain.collection.repository.CollectionRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    public ResponseEntity<?> chemicalFindAll(){
        try {
            List<ChemicalEntity> chemicalEntityList = collectionRepository.findAll();
            if(chemicalEntityList.isEmpty()) {
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질이 없습니다.", null));
            } else {
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질 조회 성공", chemicalEntityList));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "화학물질 조회 실패", null));
        }

    }

    public ResponseEntity chemicalFindByMolecularFormula(String molecularFormula) {
        try{
            if(!collectionRepository.existsByMolecularFormulaIgnoreCase(molecularFormula)) {
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질이 없습니다.", null));
            } else {
                ChemicalEntity chemicalEntity = collectionRepository.findByMolecularFormulaIgnoreCase(molecularFormula);
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학물질 조회 성공", chemicalEntity));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "화학물질 조회 실패", null));
        }

    }

}



