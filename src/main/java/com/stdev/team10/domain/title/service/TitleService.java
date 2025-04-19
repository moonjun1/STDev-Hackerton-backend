package com.stdev.team10.domain.title.service;

import com.stdev.team10.domain.chemical.repository.ChemicalRepository;
import com.stdev.team10.domain.ranking.entity.FormulaSearchHistory;
import com.stdev.team10.domain.ranking.repository.SearchHistoryRepository;
import com.stdev.team10.domain.title.dto.TitleDto;
import com.stdev.team10.domain.title.entity.TitleEntity;
import com.stdev.team10.domain.title.entity.UserTitleEntity;
import com.stdev.team10.domain.title.repository.TitleRepository;
import com.stdev.team10.domain.title.repository.UserTitleRepository;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TitleService {

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private UserTitleRepository userTitleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    /**
     * 모든 가능한 칭호 목록을 가져오는 메소드
     */
    public ResponseEntity<?> getAllTitles(Long userId) {
        try {
            // 사용자 조회
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.response(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", null));
            }

            UserEntity user = userOptional.get();

            // 모든 칭호 조회
            List<TitleEntity> allTitles = titleRepository.findAll();

            // 사용자가 획득한 칭호 조회
            List<UserTitleEntity> userTitles = userTitleRepository.findByUser(user);

            // 현재 활성화된 칭호 찾기
            Optional<UserTitleEntity> activeTitle = userTitleRepository.findByUserAndIsActiveTrue(user);

            // 사용자가 획득한 칭호 ID 맵 생성
            Map<Long, UserTitleEntity> userTitleMap = userTitles.stream()
                    .collect(Collectors.toMap(ut -> ut.getTitle().getTitleId(), ut -> ut));

            // TitleDto 목록 생성
            List<TitleDto> titleDtos = allTitles.stream()
                    .map(title -> {
                        UserTitleEntity userTitle = userTitleMap.get(title.getTitleId());
                        boolean isUnlocked = userTitle != null;
                        boolean isActive = isUnlocked && activeTitle.isPresent() &&
                                activeTitle.get().getTitle().getTitleId().equals(title.getTitleId());

                        return TitleDto.builder()
                                .titleId(title.getTitleId())
                                .titleName(title.getTitleName())
                                .description(title.getDescription())
                                .unlockCondition(title.getUnlockCondition())
                                .isUnlocked(isUnlocked)
                                .isActive(isActive)
                                .build();
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK, "칭호 목록 조회 성공", titleDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "칭호 목록 조회 실패: " + e.getMessage(), null));
        }
    }

    /**
     * 특정 칭호를 활성화하는 메소드
     */
    @Transactional
    public ResponseEntity<?> activateTitle(Long userId, Long titleId) {
        try {
            // 사용자 조회
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.response(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", null));
            }

            UserEntity user = userOptional.get();

            // 칭호 조회
            Optional<TitleEntity> titleOptional = titleRepository.findById(titleId);
            if (!titleOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.response(HttpStatus.NOT_FOUND, "칭호를 찾을 수 없습니다.", null));
            }

            TitleEntity title = titleOptional.get();

            // 사용자가 해당 칭호를 보유하고 있는지 확인
            Optional<UserTitleEntity> userTitleOptional = userTitleRepository.findByUserAndTitle(user, title);
            if (!userTitleOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "사용자가 해당 칭호를 보유하고 있지 않습니다.", null));
            }

            // 기존 활성 칭호가 있으면 비활성화
            Optional<UserTitleEntity> activeTitle = userTitleRepository.findByUserAndIsActiveTrue(user);
            activeTitle.ifPresent(ut -> ut.setIsActive(false));

            // 새 칭호 활성화
            UserTitleEntity userTitle = userTitleOptional.get();
            userTitle.setIsActive(true);
            userTitleRepository.save(userTitle);

            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK, "칭호 활성화 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "칭호 활성화 실패: " + e.getMessage(), null));
        }
    }

    /**
     * 사용자의 화학식 검색 기록을 확인하고 조건에 맞는 칭호 부여
     */
    @Transactional
    public void checkAndAwardTitles(UserEntity user, String foundFormula) {
        try {
            // 기본 칭호: 첫 발견
            awardFirstDiscoveryTitle(user);

            // 교육 수준별 칭호 체크
            checkEducationLevelTitles(user);

            // 특정 화학물질 기반 칭호 체크
            checkSpecificChemicalTitles(user, foundFormula);

            // 원소 기반 칭호 체크
            checkElementBasedTitles(user);

        } catch (Exception e) {
            // 칭호 부여 중 오류가 발생해도 주요 기능은 계속 작동하도록 함
            System.err.println("칭호 부여 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 첫 번째 화학물질 발견 칭호 부여
     */
    private void awardFirstDiscoveryTitle(UserEntity user) {
        // 첫 발견 칭호 체크 (첫 번째 성공적인 검색)
        long successCount = searchHistoryRepository.countDistinctSuccessfulFormulasByUser(user);
        if (successCount == 1) {
            awardTitle(user, "첫 발견");
        }
    }

    /**
     * 교육 수준별 칭호 체크 및 부여
     */
    private void checkEducationLevelTitles(UserEntity user) {
        // 각 교육 수준별 화학물질 수 확인
        long elementaryCount = countFoundChemicalsByEducationLevel(user, "초등학교");
        long middleCount = countFoundChemicalsByEducationLevel(user, "중학교");
        long highCount = countFoundChemicalsByEducationLevel(user, "고등학교");

        // 총 교육 수준별 화학물질 수 (데이터베이스에서 가져오거나 하드코딩)
        long totalElementaryCount = chemicalRepository.countByEducationLevel("초등학교");
        long totalMiddleCount = chemicalRepository.countByEducationLevel("중학교");
        long totalHighCount = chemicalRepository.countByEducationLevel("고등학교");
        long totalCount = totalElementaryCount + totalMiddleCount + totalHighCount;

        // 초등교육완성 칭호
        if (elementaryCount >= totalElementaryCount && totalElementaryCount > 0) {
            awardTitle(user, "초등교육완성");
        }

        // 중등교육완성 칭호
        if (middleCount >= totalMiddleCount && totalMiddleCount > 0) {
            awardTitle(user, "중등교육완성");
        }

        // 고등교육완성 칭호
        if (highCount >= totalHighCount && totalHighCount > 0) {
            awardTitle(user, "고등교육완성");
        }

        // 화학의 대가 칭호
        long totalFoundCount = elementaryCount + middleCount + highCount;
        if (totalFoundCount >= totalCount && totalCount > 0) {
            awardTitle(user, "화학의 대가");
        }
    }

    /**
     * 특정 화학물질 관련 칭호 체크 및 부여
     */
    private void checkSpecificChemicalTitles(UserEntity user, String foundFormula) {
        // 물과 불의 연금술사 칭호 체크 (H2O와 관련된 화합물 5개 이상 발견)
        if (foundFormula.contains("H") && foundFormula.contains("O")) {
            long waterRelatedCount = countFoundChemicalsWithElements(user, Arrays.asList("H", "O"));
            if (waterRelatedCount >= 5) {
                awardTitle(user, "물과 불의 연금술사");
            }
        }

        // 산과 염기의 마술사 칭호 체크
        long acidBaseCount = countFoundAcidBaseChemicals(user);
        if (acidBaseCount >= 10) {
            awardTitle(user, "산과 염기의 마술사");
        }

        // 유기화학자 칭호 체크
        if (foundFormula.contains("C")) {
            long carbonCount = countFoundChemicalsWithElements(user, Arrays.asList("C"));
            if (carbonCount >= 15) {
                awardTitle(user, "유기화학자");
            }
        }
    }

    /**
     * 원소 기반 칭호 체크 및 부여
     */
    private void checkElementBasedTitles(UserEntity user) {
        // 원소 수집가 칭호 체크
        Set<String> distinctElements = getDistinctElementsFoundByUser(user);
        if (distinctElements.size() >= 10) {
            awardTitle(user, "원소 수집가");
        }

        // 주기율표 완성자 칭호 체크
        if (hasFoundElementsFromAllGroups(user)) {
            awardTitle(user, "주기율표 완성자");
        }
    }

    /**
     * 특정 교육 수준의 화학물질을 사용자가 발견한 수 계산
     */
    private long countFoundChemicalsByEducationLevel(UserEntity user, String educationLevel) {
        // 검색 기록에서 성공적으로 매치된 화학식 추출
        List<String> matchedFormulas = getSuccessfullyMatchedFormulas(user);

        // 해당 교육 수준의 화학물질 중 사용자가 발견한 것의 수 계산
        return chemicalRepository.countByMolecularFormulaInAndEducationLevel(matchedFormulas, educationLevel);
    }

    /**
     * 사용자가 특정 원소를 포함한 화학물질을 발견한 수 계산
     */
    private long countFoundChemicalsWithElements(UserEntity user, List<String> elements) {
        // 검색 기록에서 성공적으로 매치된 화학식 추출
        List<String> matchedFormulas = getSuccessfullyMatchedFormulas(user);

        // 해당 원소를 포함하는 화학물질 중 사용자가 발견한 것의 수 계산
        return matchedFormulas.stream()
                .filter(formula -> elements.stream().allMatch(formula::contains))
                .count();
    }

    /**
     * 사용자가 발견한 산/염기 화학물질 수 계산
     */
    private long countFoundAcidBaseChemicals(UserEntity user) {
        // 검색 기록에서 성공적으로 매치된 화학식 추출
        List<String> matchedFormulas = getSuccessfullyMatchedFormulas(user);

        // 산/염기 관련 화학물질 식별 (간단한 예시)
        List<String> acidBaseKeywords = Arrays.asList("acid", "base", "OH", "H3O");

        // 해당 산/염기 화학물질 중 사용자가 발견한 것의 수 계산
        return chemicalRepository.countByMolecularFormulaInAndChemicalNameEnContainingAny(
                matchedFormulas, acidBaseKeywords);
    }

    /**
     * 사용자가 발견한 화학물질에서 사용된 모든 고유 원소 추출
     */
    private Set<String> getDistinctElementsFoundByUser(UserEntity user) {
        // 검색 기록에서 성공적으로 매치된 화학식 추출
        List<String> matchedFormulas = getSuccessfullyMatchedFormulas(user);

        // 모든 화학식에서 원소 추출 (간단한 예시)
        Set<String> elements = new HashSet<>();
        for (String formula : matchedFormulas) {
            // 화학식에서 원소 추출 로직 (실제로는 더 복잡한 파싱 필요)
            Matcher matcher = Pattern.compile("[A-Z][a-z]?").matcher(formula);
            while (matcher.find()) {
                elements.add(matcher.group());
            }
        }

        return elements;
    }

    /**
     * 사용자가 모든 주기율표 그룹에서 원소를 발견했는지 확인
     */
    private boolean hasFoundElementsFromAllGroups(UserEntity user) {
        // 주기율표 그룹별 대표 원소 (간단한 예시)
        Map<Integer, List<String>> periodicGroups = new HashMap<>();
        periodicGroups.put(1, Arrays.asList("H", "Li", "Na", "K"));
        periodicGroups.put(2, Arrays.asList("Be", "Mg", "Ca"));
        // ... 다른 그룹들

        // 사용자가 발견한 원소
        Set<String> userElements = getDistinctElementsFoundByUser(user);

        // 모든 그룹에서 최소 하나의 원소를 발견했는지 확인
        for (List<String> groupElements : periodicGroups.values()) {
            if (groupElements.stream().noneMatch(userElements::contains)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 사용자가 성공적으로 매치한 화학식 목록 가져오기
     */
    private List<String> getSuccessfullyMatchedFormulas(UserEntity user) {
        List<FormulaSearchHistory> histories = searchHistoryRepository.findByUserAndSuccessfulTrue(user);
        return histories.stream()
                .map(FormulaSearchHistory::getMatchedFormula)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 특정 칭호 부여
     */
    private void awardTitle(UserEntity user, String titleName) {
        // 해당 칭호 조회
        Optional<TitleEntity> titleOptional = titleRepository.findByTitleName(titleName);
        if (!titleOptional.isPresent()) {
            System.err.println("칭호를 찾을 수 없음: " + titleName);
            return;
        }

        TitleEntity title = titleOptional.get();

        // 이미 해당 칭호를 가지고 있는지 확인
        boolean alreadyHasTitle = userTitleRepository.existsByUserAndTitle(user, title);
        if (alreadyHasTitle) {
            return; // 이미 가지고 있으면 중복 부여하지 않음
        }

        // 새 칭호 부여
        UserTitleEntity userTitle = UserTitleEntity.builder()
                .user(user)
                .title(title)
                .acquiredAt(LocalDateTime.now())
                .isActive(false) // 기본적으로 비활성 상태로 부여
                .build();

        userTitleRepository.save(userTitle);
        System.out.println("사용자 " + user.getUsername() + "에게 칭호 '" + titleName + "' 부여됨");
    }

    /**
     * 데이터베이스 초기화 - 기본 칭호 생성
     */
    @Transactional
    public void initializeTitles() {
        // 이미 칭호가 존재하는지 확인
        if (titleRepository.count() > 0) {
            return;
        }

        // 기본 칭호 생성
        List<TitleEntity> defaultTitles = Arrays.asList(
                TitleEntity.builder()
                        .titleName("초등교육완성")
                        .description("초등학교 수준의 화학 물질을 모두 찾은 화학자")
                        .unlockCondition("초등학교 수준의 화학 물질 모두 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("중등교육완성")
                        .description("중학교 수준의 화학 물질을 모두 찾은 학구파")
                        .unlockCondition("중학교 수준의 화학 물질 모두 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("고등교육완성")
                        .description("고등학교 수준의 화학 물질을 모두 찾은 전문가")
                        .unlockCondition("고등학교 수준의 화학 물질 모두 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("화학의 대가")
                        .description("모든 수준의 화학 물질을 완벽히 마스터한 대가")
                        .unlockCondition("모든 수준의 화학 물질 100% 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("원소 수집가")
                        .description("다양한 원소로 구성된 화학 물질을 많이 발견함")
                        .unlockCondition("10개 이상의 서로 다른 원소를 포함한 화학 물질 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("물과 불의 연금술사")
                        .description("물(H2O)과 관련된 화합물에 특별한 관심을 가진 연구자")
                        .unlockCondition("물(H2O)을 포함한 화합물 5개 이상 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("첫 발견")
                        .description("화학 세계에 첫 발을 내딛은 초보 탐험가")
                        .unlockCondition("첫 번째 화학 물질 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("산과 염기의 마술사")
                        .description("산과 염기 화합물에 대한 깊은 이해를 가진 전문가")
                        .unlockCondition("산과 염기 관련 화합물 10개 이상 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("유기화학자")
                        .description("탄소 화합물에 대한 특별한 지식을 가진 연구자")
                        .unlockCondition("탄소(C)를 포함한 화합물 15개 이상 발견하기")
                        .build(),
                TitleEntity.builder()
                        .titleName("주기율표 완성자")
                        .description("모든 주기율표의 원소를 포함한 화합물을 발견함")
                        .unlockCondition("주기율표의 모든 그룹에서 적어도 하나의 원소를 포함한 화합물 발견하기")
                        .build()
        );

        titleRepository.saveAll(defaultTitles);
        System.out.println("기본 칭호 초기화 완료");
    }
}