package com.stdev.team10.domain.ranking.repository;

import com.stdev.team10.domain.ranking.entity.FormulaSearchHistory;
import com.stdev.team10.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<FormulaSearchHistory, Long> {

    // 특정 사용자의 성공한 검색 횟수 조회 (중복 제외)
    @Query("SELECT COUNT(DISTINCT h.matchedFormula) FROM FormulaSearchHistory h " +
            "WHERE h.user = :user AND h.successful = true")
    long countDistinctSuccessfulFormulasByUser(@Param("user") UserEntity user);

    // 특정 조직의 사용자들이 성공한 검색 횟수 조회 (중복 제외)
    @Query("SELECT u.organization, COUNT(DISTINCT CONCAT(h.user.id, '-', h.matchedFormula)) " +
            "FROM FormulaSearchHistory h JOIN h.user u " +
            "WHERE h.successful = true GROUP BY u.organization ORDER BY COUNT(DISTINCT CONCAT(h.user.id, '-', h.matchedFormula)) DESC")
    List<Object[]> countDistinctSuccessfulSearchesByOrganization();

    // 사용자별 성공한 검색 횟수 조회 (중복 제외)
    @Query("SELECT h.user, COUNT(DISTINCT h.matchedFormula) FROM FormulaSearchHistory h " +
            "WHERE h.successful = true GROUP BY h.user ORDER BY COUNT(DISTINCT h.matchedFormula) DESC")
    List<Object[]> findUsersByDistinctSuccessfulSearches();

    // 특정 사용자가 특정 화학식을 이미 성공적으로 검색했는지 확인
    boolean existsByUserAndMatchedFormulaAndSuccessfulTrue(UserEntity user, String matchedFormula);

    // 기존 코드에 추가
    List<FormulaSearchHistory> findByUserAndMatchedFormulaAndSuccessfulTrue(UserEntity user, String matchedFormula);
    List<FormulaSearchHistory> findByUserAndSuccessfulTrue(UserEntity user);
}