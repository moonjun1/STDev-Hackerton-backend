package com.stdev.team10.domain.collection.repository;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<ChemicalEntity, Long> {
    boolean existsBychemicalNameEn(String chemicalNameEn);

    ChemicalEntity findByMolecularFormulaIgnoreCase(String molecularFormula); //화학식으로 조회
    boolean existsByMolecularFormulaIgnoreCase(String molecularFormula);//화학식으로 존재 유무 확인
}
