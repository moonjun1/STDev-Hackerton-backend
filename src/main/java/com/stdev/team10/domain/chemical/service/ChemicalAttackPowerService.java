package com.stdev.team10.domain.chemical.service;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import com.stdev.team10.domain.chemical.repository.ChemicalRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class ChemicalAttackPowerService {

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    /**
     * 모든 화학물질에 공격력 값 부여 (간소화 버전)
     */
    @Transactional
    public void assignAttackPowerToAllChemicals() {
        List<ChemicalEntity> chemicals = chemicalRepository.findAll();
        int batchSize = 0;

        for (ChemicalEntity chemical : chemicals) {
            // 이미 공격력이 설정되어 있고 0이 아니면 건너뜀
            if (chemical.getAttackPower() != null && chemical.getAttackPower() > 0) {
                continue;
            }

            int attackPower = determineAttackPower(chemical);
            chemical.setAttackPower(attackPower);

            System.out.println("화학물질: " + chemical.getChemicalNameKo() +
                    ", 분자식: " + chemical.getMolecularFormula() +
                    ", 공격력: " + attackPower);

            // 50개마다 저장하고 클리어 (메모리 관리)
            if (++batchSize % 50 == 0) {
                chemicalRepository.saveAll(chemicals.subList(batchSize - 50, batchSize));
                System.out.println(batchSize + "개 화학물질 처리 완료");
            }
        }

        // 남은 항목 저장
        chemicalRepository.saveAll(chemicals);
    }

    /**
     * 랜덤 공격력 값 부여 (테스트용)
     */
    @Transactional
    public void assignRandomAttackPower() {
        List<ChemicalEntity> chemicals = chemicalRepository.findAll();

        for (ChemicalEntity chemical : chemicals) {
            int attackPower = random.nextInt(100) + 1; // 1~100 사이 랜덤 값
            chemical.setAttackPower(attackPower);
        }

        chemicalRepository.saveAll(chemicals);
        System.out.println("총 " + chemicals.size() + "개 화학물질에 랜덤 공격력 부여 완료");
    }

    /**
     * 간소화된 ChatGPT API 활용 - 경도와 위험도만 고려
     */
    private int determineAttackPower(ChemicalEntity chemical) {
        try {
            String prompt = createSimplifiedPrompt(chemical);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // GPT-3.5-turbo 모델 사용 (더 빠름)
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo", // 더 빠른 모델 사용
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "당신은 화학물질의 경도와 인체 위험도를 분석하여 1~100 사이의 공격력 값을 추천하는 전문가입니다."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "temperature", 0.3, // 더 일관된 응답을 위해 낮은 temperature 사용
                    "max_tokens", 150   // 응답 길이 제한으로 속도 향상
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // API 응답 처리
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            String aiResponse = extractResponseContent(response);

            // 응답에서 숫자 추출 - 간소화된 방식
            int attackPower = parseSimplifiedAttackPower(aiResponse);
            return attackPower;

        } catch (Exception e) {
            System.err.println("화학물질 " + chemical.getChemicalNameKo() + " 공격력 결정 오류: " + e.getMessage());

            // 오류 시 간단한 휴리스틱 사용
            // 분자식의 길이에 따라 기본값 조정 (복잡한 물질 = 높은 공격력)
            String formula = chemical.getMolecularFormula();
            int baseValue = 50; // 기본값

            if (formula != null) {
                // 분자식 길이에 따라 공격력 조정
                baseValue += Math.min(formula.length() * 2, 30);

                // 특정 위험 원소 포함 여부에 따라 추가 점수
                if (formula.contains("Cl") || formula.contains("F") ||
                        formula.contains("Br") || formula.contains("I")) {
                    baseValue += 10; // 할로겐 원소
                }

                if (formula.contains("S") || formula.contains("P")) {
                    baseValue += 5; // 황, 인
                }

                if (formula.contains("Hg") || formula.contains("Pb") ||
                        formula.contains("As") || formula.contains("Cd")) {
                    baseValue += 15; // 독성 중금속
                }
            }

            // 최종 범위 조정
            return Math.max(10, Math.min(baseValue, 90));
        }
    }

    /**
     * 간소화된 프롬프트 - 경도와 위험도만 고려
     */
    private String createSimplifiedPrompt(ChemicalEntity chemical) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("화학물질 정보:\n");
        promptBuilder.append("- 이름: ").append(chemical.getChemicalNameKo()).append(" (").append(chemical.getChemicalNameEn()).append(")\n");
        promptBuilder.append("- 분자식: ").append(chemical.getMolecularFormula()).append("\n\n");

        promptBuilder.append("이 화학물질의 경도(단단함)와 인체 위험도만 고려하여 1~100 사이의 공격력 값을 결정해주세요.\n");
        promptBuilder.append("- 경도: 단단할수록 높은 점수 (예: 다이아몬드, 금속 > 액체 > 기체)\n");
        promptBuilder.append("- 인체 위험도: 독성, 부식성, 폭발성이 높을수록 높은 점수\n\n");

        promptBuilder.append("다음 형식으로 답변해주세요: '공격력: [숫자]'");

        return promptBuilder.toString();
    }

    /**
     * 간소화된 공격력 값 파싱
     */
    private int parseSimplifiedAttackPower(String response) {
        try {
            // "공격력: [숫자]" 패턴 검색
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("공격력\\s*:\\s*(\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                int value = Integer.parseInt(matcher.group(1));
                return Math.max(1, Math.min(value, 100)); // 1~100 범위로 제한
            }

            // 패턴이 없으면 일반 숫자 추출
            String numericValue = response.replaceAll("[^0-9]", "");
            if (!numericValue.isEmpty()) {
                // 숫자가 여러 개일 경우 최대 두 자리 수만 사용
                if (numericValue.length() > 2) {
                    numericValue = numericValue.substring(0, 2);
                }

                int value = Integer.parseInt(numericValue);
                return Math.max(1, Math.min(value, 100));
            }

            // 기본값 반환
            return 50;
        } catch (Exception e) {
            return 50; // 파싱 실패 시 중간값
        }
    }

    /**
     * API 응답에서 콘텐츠 추출
     */
    private String extractResponseContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            throw new RuntimeException("API 응답 형식이 올바르지 않습니다: " + e.getMessage());
        }
    }

    /**
     * 분자식으로 화학물질의 공격력을 조회
     */
    public ResponseEntity<?> getChemicalAttackPower(String molecularFormula) {
        try {
            ChemicalEntity chemical = chemicalRepository.findByMolecularFormula(molecularFormula);

            if (chemical == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.response(HttpStatus.NOT_FOUND,
                                "해당 분자식의 화학물질을 찾을 수 없습니다: " + molecularFormula, null));
            }

            // 공격력이 설정되지 않은 경우 null 대신 0 반환
            Integer attackPower = chemical.getAttackPower() != null ? chemical.getAttackPower() : 0;

            // 응답 데이터 구성
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("molecularFormula", chemical.getMolecularFormula());
            responseData.put("chemicalNameKo", chemical.getChemicalNameKo());
            responseData.put("chemicalNameEn", chemical.getChemicalNameEn());
            responseData.put("attackPower", attackPower);

            return ResponseEntity.ok()
                    .body(ResponseDto.response(HttpStatus.OK, "화학물질 공격력 조회 성공", responseData));
        } catch (Exception e) {
            log.error("화학물질 공격력 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR,
                            "화학물질 공격력 조회 실패: " + e.getMessage(), null));
        }
    }
}