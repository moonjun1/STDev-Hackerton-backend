package com.stdev.team10.domain.point.repository;

import com.stdev.team10.domain.point.entity.PointUserElementStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointUserElementStatusRepository extends JpaRepository<PointUserElementStatusEntity, Long> {
    boolean existsByMolecularFormulaContaining(String molecularFormula);
    List<PointUserElementStatusEntity> findByUserId(Long userId);
}
