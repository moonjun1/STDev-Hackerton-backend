package com.stdev.team10.domain.title.repository;

import com.stdev.team10.domain.title.entity.TitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitleRepository extends JpaRepository<TitleEntity, Long> {
    Optional<TitleEntity> findByTitleName(String titleName);
}