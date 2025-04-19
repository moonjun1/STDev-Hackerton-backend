package com.stdev.team10.domain.chemical.service;

import com.stdev.team10.domain.chemical.dto.UserFoundFormulaDto;
import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import com.stdev.team10.domain.chemical.repository.ChemicalRepository;
import com.stdev.team10.domain.ranking.entity.FormulaSearchHistory;
import com.stdev.team10.domain.ranking.repository.SearchHistoryRepository;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserChemicalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    /**
     * 사용자가 성공적으로 발견한 모든 화학식 조회
     */
    public ResponseEntity<?> getUserFoundFormulas(Long userId) {
        try {
            // 사용자 조회
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.response(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", null));
            }

            UserEntity user = userOptional.get();

            // 사용자가 성공적으로 검색한 모든 화학식 기록 조회
            List<FormulaSearchHistory> successHistories =
                    searchHistoryRepository.findByUserAndSuccessfulTrue(user);

            // 중복 제거 (같은 화학식을 여러번 발견한 경우)
            List<String> distinctFormulas = successHistories.stream()
                    .map(FormulaSearchHistory::getMatchedFormula)
                    .distinct()
                    .collect(Collectors.toList());

            // 각 화학식에 대한 상세 정보 조회
            List<UserFoundFormulaDto> foundFormulas = new ArrayList<>();
            for (String formula : distinctFormulas) {
                ChemicalEntity chemical = chemicalRepository.findByMolecularFormula(formula);
                if (chemical != null) {
                    UserFoundFormulaDto formulaDto = UserFoundFormulaDto.builder()
                            .molecularFormula(chemical.getMolecularFormula())
                            .chemicalNameKo(chemical.getChemicalNameKo())
                            .chemicalNameEn(chemical.getChemicalNameEn())
                            .educationLevel(chemical.getEducationLevel())
                            .chemicalDescriptionKo(chemical.getChemicalDescriptionKo())
                            .build();

                    foundFormulas.add(formulaDto);
                }
            }

            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK,
                            "사용자가 발견한 화학식 조회 성공", foundFormulas));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "사용자 화학식 조회 실패: " + e.getMessage(), null));
        }
    }
}