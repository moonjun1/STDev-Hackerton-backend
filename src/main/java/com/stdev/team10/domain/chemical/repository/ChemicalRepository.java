package com.stdev.team10.domain.chemical.repository;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChemicalRepository extends JpaRepository<ChemicalEntity, Long> {
    boolean existsBychemicalNameEn(String chemicalNameEn);

    // 분자식으로 화학 물질을 검색하는 메서드 추가
    ChemicalEntity findByMolecularFormula(String molecularFormula);

    // 여러 분자식으로 화학 물질 검색
    List<ChemicalEntity> findByMolecularFormulaIn(List<String> molecularFormulas);
    long countByEducationLevel(String educationLevel);

    long countByMolecularFormulaInAndEducationLevel(List<String> molecularFormulas, String educationLevel);

    // 수정된 메서드
    @Query("SELECT COUNT(c) FROM ChemicalEntity c WHERE c.molecularFormula IN :formulas AND " +
            "(c.chemicalNameEn IN :keywords OR LOWER(c.chemicalNameEn) LIKE CONCAT('%', 'acid', '%') OR LOWER(c.chemicalNameEn) LIKE CONCAT('%', 'base', '%'))")
    long countByMolecularFormulaInAndChemicalNameEnContainingAny(
            @Param("formulas") List<String> formulas,
            @Param("keywords") List<String> keywords);


}
