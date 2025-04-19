package com.stdev.team10.domain.title.service;

import com.stdev.team10.domain.ranking.entity.FormulaSearchHistory;
import com.stdev.team10.domain.ranking.repository.SearchHistoryRepository;
import com.stdev.team10.domain.title.dto.AiTitleDto;
import com.stdev.team10.domain.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiTitleService {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 사용자가 발견한 화학물질 목록을 기반으로 AI 칭호 생성
     */
    public List<AiTitleDto> generateAiTitles(UserEntity user) {
        // 사용자가 성공적으로 발견한 화학물질 목록 가져오기
        List<FormulaSearchHistory> searchHistories =
                searchHistoryRepository.findByUserAndSuccessfulTrue(user);

        List<String> discoveredFormulas = searchHistories.stream()
                .map(FormulaSearchHistory::getMatchedFormula)
                .distinct()
                .collect(Collectors.toList());

        if (discoveredFormulas.isEmpty()) {
            return List.of(); // 발견한 화학물질이 없는 경우 빈 목록 반환
        }

        // ChatGPT API 호출을 위한 프롬프트 생성
        String prompt = createPrompt(user.getUserName(), discoveredFormulas);

        // API 호출
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 화학 전문가로, 사용자의 화학물질 발견 기록을 분석하여 창의적이고 의미 있는 칭호를 생성합니다."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // API 응답 처리
        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
        String aiResponse = extractResponseContent(response);

        // 응답에서 칭호 목록 파싱
        return parseAiTitles(aiResponse);
    }

    /**
     * API 요청을 위한 프롬프트 생성
     */
    private String createPrompt(String username, List<String> formulas) {
        return String.format(
                "사용자 '%s'가 다음 화학물질들을 발견했습니다: %s\n\n" +
                        "이 화학물질 목록을 분석하여 사용자에게 어울리는 특별하고 창의적인 칭호 3개를 생성해주세요. " +
                        "각 칭호에는 칭호 이름, 설명, 그리고 획득 조건이 포함되어야 합니다. " +
                        "화학적 특성이나 원소의 특징, 화학물질의 역사적/문화적 의미 등을 고려해서 만들어주세요. " +
                        "응답은 다음 형식으로 해주세요:\n\n" +
                        "1. [칭호 이름]\n" +
                        "   - 설명: [칭호에 대한 설명]\n" +
                        "   - 획득 조건: [어떻게 이 칭호를 획득했는지]\n\n" +
                        "2. [칭호 이름]\n" +
                        "   - 설명: [칭호에 대한 설명]\n" +
                        "   - 획득 조건: [어떻게 이 칭호를 획득했는지]\n\n" +
                        "3. [칭호 이름]\n" +
                        "   - 설명: [칭호에 대한 설명]\n" +
                        "   - 획득 조건: [어떻게 이 칭호를 획득했는지]",
                username,
                String.join(", ", formulas)
        );
    }

    /**
     * API 응답에서 컨텐츠 추출
     */
    private String extractResponseContent(Map<String, Object> response) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    /**
     * AI 응답에서 칭호 목록 파싱
     */
    private List<AiTitleDto> parseAiTitles(String aiResponse) {
        // 간단한 구현: 응답을 줄별로 파싱하여 칭호 추출
        // 실제 구현에서는 더 견고한 파싱 로직이 필요할 수 있습니다

        String[] lines = aiResponse.split("\n");
        List<AiTitleDto> titles = new java.util.ArrayList<>();

        AiTitleDto currentTitle = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // 새로운 칭호 시작
            if (line.matches("\\d+\\.\\s+.*")) {
                if (currentTitle != null) {
                    titles.add(currentTitle);
                }

                String titleName = line.replaceFirst("\\d+\\.\\s+", "");
                currentTitle = new AiTitleDto();
                currentTitle.setTitleName(titleName);
            }
            // 설명
            else if (line.startsWith("- 설명:")) {
                if (currentTitle != null) {
                    currentTitle.setDescription(line.substring("- 설명:".length()).trim());
                }
            }
            // 획득 조건
            else if (line.startsWith("- 획득 조건:")) {
                if (currentTitle != null) {
                    currentTitle.setUnlockCondition(line.substring("- 획득 조건:".length()).trim());
                }
            }
        }

        // 마지막 칭호 추가
        if (currentTitle != null) {
            titles.add(currentTitle);
        }

        return titles;
    }
}