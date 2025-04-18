package com.stdev.team10.domain.chemical.repository;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChemicalRepository extends JpaRepository<ChemicalEntity, Long> {

    ChemicalEntity findByMolecularFormulaIgnoreCase(String molecularFormula); //화학식으로 조회
    boolean existsByMolecularFormulaIgnoreCase(String molecularFormula);//화학식으로 존재 유무 확인

}
