package com.stdev.team10.domain.chemical.service;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import com.stdev.team10.domain.chemical.repository.ChemicalRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChemicalService {
    @Autowired
    private ChemicalRepository chemicalRepository;
}
