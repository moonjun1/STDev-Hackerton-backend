package com.stdev.team10.domain.chemical.service;


import com.stdev.team10.domain.chemical.dto.ChemicalDto;
import com.stdev.team10.domain.chemical.dto.ChemicalFormulaDto;
import com.stdev.team10.domain.chemical.dto.ChemicalSearchResponseDto;
import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import com.stdev.team10.domain.chemical.repository.ChemicalRepository;
import com.stdev.team10.domain.ranking.entity.FormulaSearchHistory;
import com.stdev.team10.domain.ranking.repository.SearchHistoryRepository;
import com.stdev.team10.domain.title.service.TitleService;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChemicalFormulaService {

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TitleService titleService;

    /**
     * 화학식을 검색하는 메서드 (사용자 ID를 제공하는 경우)
     */
    public ResponseEntity<?> searchChemicalByFormula(ChemicalFormulaDto formulaDto, Long userId) {
        String input = formulaDto.getFormula().toLowerCase();

        // 사용자 조회
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.response(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", null));
        }

        UserEntity user = userOptional.get();

        // "h + h + o" 패턴을 파싱하여 원소별 개수 계산
        Map<String, Integer> elementCountMap = parseElementsFromInput(input);

        // 가능한 모든 화학식 조합 생성
        List<String> possibleFormulas = generateAllPossibleFormulas(elementCountMap);

        try {
            // 모든 가능한 조합으로 한 번에 검색
            List<ChemicalEntity> foundChemicals = chemicalRepository.findByMolecularFormulaIn(possibleFormulas);

            if (!foundChemicals.isEmpty()) {
                // 첫 번째 발견된 화학물질 사용
                ChemicalEntity chemicalEntity = foundChemicals.get(0);
                ChemicalDto chemicalDto = convertToDto(chemicalEntity);

                // 어떤 화학식으로 발견되었는지 확인
                String foundFormula = chemicalEntity.getMolecularFormula();

                // 이미 성공한 동일 화학식 검색 여부 확인
                boolean alreadySuccessful = searchHistoryRepository.existsByUserAndMatchedFormulaAndSuccessfulTrue(user, foundFormula);

                // 첫 성공인 경우에만 이력 추가 (중복 방지)
                if (!alreadySuccessful) {
                    // 검색 성공 이력 저장
                    FormulaSearchHistory searchHistory = FormulaSearchHistory.builder()
                            .user(user)
                            .matchedFormula(foundFormula)
                            .successful(true)
                            .build();
                    searchHistoryRepository.save(searchHistory);

                    // 칭호 체크 및 부여
                    titleService.checkAndAwardTitles(user, foundFormula);
                }

                ChemicalSearchResponseDto responseDto = ChemicalSearchResponseDto.builder()
                        .success(true)
                        .message("화학 물질을 찾았습니다: " + foundFormula)
                        .chemical(chemicalDto)
                        .originalInput(input)
                        .convertedFormula(foundFormula)
                        .build();

                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK,
                        alreadySuccessful ? "화학 물질 검색 성공 (이미 검색한 화학식입니다)" : "화학 물질 검색 성공",
                        responseDto));
            } else {
                // 표준 화학식 (알파벳 순서로 정렬)
                String standardFormula = convertToMolecularFormula(elementCountMap);

                // 검색 실패 이력 저장
                FormulaSearchHistory searchHistory = FormulaSearchHistory.builder()
                        .user(user)
                        .matchedFormula(null)
                        .successful(false)
                        .build();
                searchHistoryRepository.save(searchHistory);

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.response(HttpStatus.NOT_FOUND,
                        "해당 원소 조합으로 화학 물질을 찾을 수 없습니다: " + standardFormula, null));
            }
        } catch (Exception e) {
            // 오류 발생 시 검색 실패 이력 저장
            FormulaSearchHistory searchHistory = FormulaSearchHistory.builder()
                    .user(user)
                    .matchedFormula(null)
                    .successful(false)
                    .build();
            searchHistoryRepository.save(searchHistory);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "화학 물질 검색 실패: " + e.getMessage(), null));
        }
    }

    /**
     * 화학식을 검색하는 메서드 (익명 사용자용)
     */
    public ResponseEntity<?> searchChemicalByFormula(ChemicalFormulaDto formulaDto) {
        String input = formulaDto.getFormula().toLowerCase();

        // "h + h + o" 패턴을 파싱하여 원소별 개수 계산
        Map<String, Integer> elementCountMap = parseElementsFromInput(input);

        // 가능한 모든 화학식 조합 생성
        List<String> possibleFormulas = generateAllPossibleFormulas(elementCountMap);

        try {
            // 모든 가능한 조합으로 한 번에 검색
            List<ChemicalEntity> foundChemicals = chemicalRepository.findByMolecularFormulaIn(possibleFormulas);

            if (!foundChemicals.isEmpty()) {
                // 첫 번째 발견된 화학물질 사용
                ChemicalEntity chemicalEntity = foundChemicals.get(0);
                ChemicalDto chemicalDto = convertToDto(chemicalEntity);

                // 어떤 화학식으로 발견되었는지 확인
                String foundFormula = chemicalEntity.getMolecularFormula();

                ChemicalSearchResponseDto responseDto = ChemicalSearchResponseDto.builder()
                        .success(true)
                        .message("화학 물질을 찾았습니다: " + foundFormula)
                        .chemical(chemicalDto)
                        .originalInput(input)
                        .convertedFormula(foundFormula)
                        .build();

                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "화학 물질 검색 성공", responseDto));
            } else {
                // 표준 화학식 (알파벳 순서로 정렬)
                String standardFormula = convertToMolecularFormula(elementCountMap);

                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.NOT_FOUND,
                        "해당 원소 조합으로 화학 물질을 찾을 수 없습니다: " + standardFormula, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "화학 물질 검색 실패: " + e.getMessage(), null));
        }
    }

    // ChemicalFormulaService.java의 convertToDto 메서드 수정
    private ChemicalDto convertToDto(ChemicalEntity entity) {
        ChemicalDto dto = new ChemicalDto();
        dto.setChemicalId(entity.getChemicalId());
        dto.setChemicalNameKo(entity.getChemicalNameKo());
        dto.setChemicalNameEn(entity.getChemicalNameEn());
        dto.setMolecularFormula(entity.getMolecularFormula());
        dto.setChemicalDescriptionKo(entity.getChemicalDescriptionKo());
        dto.setEducationLevel(entity.getEducationLevel());
        dto.setAttackPower(entity.getAttackPower());
        return dto;
    }

    // "h + h + o" 형식의 입력을 파싱하여 원소별 개수를 맵으로 반환
    private Map<String, Integer> parseElementsFromInput(String input) {
        Map<String, Integer> elementCount = new HashMap<>();
        String[] elements = input.split("\\+");
        for (String element : elements) {
            String cleanElement = element.trim().toLowerCase();
            if (!cleanElement.isEmpty()) {
                String formattedElement = cleanElement.substring(0, 1).toUpperCase();
                if (cleanElement.length() > 1) {
                    formattedElement += cleanElement.substring(1);
                }
                elementCount.put(formattedElement, elementCount.getOrDefault(formattedElement, 0) + 1);
            }
        }
        return elementCount;
    }

    // 원소별 개수를 기본적인 화학식 표기법으로 변환 (알파벳 순서)
    private String convertToMolecularFormula(Map<String, Integer> elementCount) {
        StringBuilder formula = new StringBuilder();
        List<String> sortedElements = new ArrayList<>(elementCount.keySet());
        Collections.sort(sortedElements);
        for (String element : sortedElements) {
            formula.append(element);
            int count = elementCount.get(element);
            if (count > 1) {
                formula.append(count);
            }
        }
        return formula.toString();
    }

    // 가능한 모든 화학식 조합 생성
    private List<String> generateAllPossibleFormulas(Map<String, Integer> elementCount) {
        List<String> possibleFormulas = new ArrayList<>();

        // 1. 알파벳 순서로 정렬된 기본 화학식 추가
        possibleFormulas.add(convertToMolecularFormula(elementCount));

        // 2. 화학식에서 자주 사용되는 일반적인 원소 순서 패턴 추가
        // H2O 패턴: H가 먼저
        if (elementCount.containsKey("H")) {
            StringBuilder formula = new StringBuilder("H");
            int hCount = elementCount.get("H");
            if (hCount > 1) {
                formula.append(hCount);
            }

            // 다른 원소 추가
            List<String> otherElements = new ArrayList<>();
            for (String element : elementCount.keySet()) {
                if (!element.equals("H")) {
                    otherElements.add(element);
                }
            }
            // 알파벳 순서로 다른 원소 추가
            Collections.sort(otherElements);
            for (String element : otherElements) {
                formula.append(element);
                int count = elementCount.get(element);
                if (count > 1) {
                    formula.append(count);
                }
            }
            possibleFormulas.add(formula.toString());
        }

        // CO2, NO2 패턴: O가 마지막
        if (elementCount.containsKey("O")) {
            List<String> elementsWithoutO = new ArrayList<>();
            for (String element : elementCount.keySet()) {
                if (!element.equals("O")) {
                    elementsWithoutO.add(element);
                }
            }

            if (!elementsWithoutO.isEmpty()) {
                Collections.sort(elementsWithoutO);
                StringBuilder formula = new StringBuilder();

                for (String element : elementsWithoutO) {
                    formula.append(element);
                    int count = elementCount.get(element);
                    if (count > 1) {
                        formula.append(count);
                    }
                }

                formula.append("O");
                int oCount = elementCount.get("O");
                if (oCount > 1) {
                    formula.append(oCount);
                }

                possibleFormulas.add(formula.toString());
            }
        }

        // 중복 제거 후 반환
        return new ArrayList<>(new HashSet<>(possibleFormulas));
    }
}