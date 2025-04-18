package com.stdev.team10.domain.chemical.repository;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChemicalRepository extends JpaRepository<ChemicalEntity, Long> {



}
