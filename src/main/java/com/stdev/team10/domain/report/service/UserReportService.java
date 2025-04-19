package com.stdev.team10.domain.report.service;

import com.stdev.team10.domain.chemical.entity.ChemicalEntity;
import com.stdev.team10.domain.chemical.repository.ChemicalRepository;
import com.stdev.team10.domain.ranking.entity.FormulaSearchHistory;
import com.stdev.team10.domain.ranking.repository.SearchHistoryRepository;
import com.stdev.team10.domain.report.dto.UserReportDto;
import com.stdev.team10.domain.title.entity.UserTitleEntity;
import com.stdev.team10.domain.title.repository.UserTitleRepository;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private UserTitleRepository userTitleRepository;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 사용자의 화학 활동에 대한 분석 보고서 생성
     */
    public UserReportDto generateUserReport(Long userId) {
        // 사용자 정보 조회
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        UserEntity user = userOptional.get();

        // 사용자의 화학물질 발견 기록 조회
        List<FormulaSearchHistory> searchHistories =
                searchHistoryRepository.findByUserAndSuccessfulTrue(user);

        // 사용자가 획득한 칭호 조회
        List<UserTitleEntity> userTitles = userTitleRepository.findByUser(user);

        // 사용자 활동 데이터 수집
        Map<String, Object> userData = collectUserData(user, searchHistories, userTitles);

        // ChatGPT API로 보고서 생성
        String reportContent = generateReportWithAI(userData);

        // 보고서 DTO 생성
        return UserReportDto.builder()
                .userId(user.getUserId())
                .username(user.getUserName())
                .organization(user.getGroupName())
                .generatedAt(LocalDateTime.now())
                .reportContent(reportContent)
                .build();
    }

    /**
     * 사용자 활동 데이터 수집
     */
    private Map<String, Object> collectUserData(UserEntity user,
                                                List<FormulaSearchHistory> searchHistories,
                                                List<UserTitleEntity> userTitles) {
        Map<String, Object> userData = new HashMap<>();

        // 기본 사용자 정보
        userData.put("userId", user.getUserId());
        userData.put("username", user.getUserName());
        userData.put("organization", user.getGroupName());

        // 발견한 화학물질 정보
        List<String> discoveredFormulas = searchHistories.stream()
                .map(FormulaSearchHistory::getMatchedFormula)
                .distinct()
                .collect(Collectors.toList());

        userData.put("discoveredFormulas", discoveredFormulas);
        userData.put("totalDiscovered", discoveredFormulas.size());

        // 교육 수준별 발견 현황
        Map<String, Long> educationLevelStats = new HashMap<>();
        Map<String, List<String>> educationLevelFormulas = new HashMap<>();

        List<String> elementaryFormulas = new ArrayList<>();
        List<String> middleFormulas = new ArrayList<>();
        List<String> highFormulas = new ArrayList<>();

        for (String formula : discoveredFormulas) {
            ChemicalEntity chemical = chemicalRepository.findByMolecularFormula(formula);
            if (chemical != null) {
                String educationLevel = chemical.getEducationLevel();

                if ("초등학교".equals(educationLevel)) {
                    elementaryFormulas.add(formula);
                } else if ("중학교".equals(educationLevel)) {
                    middleFormulas.add(formula);
                } else if ("고등학교".equals(educationLevel)) {
                    highFormulas.add(formula);
                }
            }
        }

        long totalElementary = chemicalRepository.countByEducationLevel("초등학교");
        long totalMiddle = chemicalRepository.countByEducationLevel("중학교");
        long totalHigh = chemicalRepository.countByEducationLevel("고등학교");

        educationLevelStats.put("초등학교", (long) elementaryFormulas.size());
        educationLevelStats.put("중학교", (long) middleFormulas.size());
        educationLevelStats.put("고등학교", (long) highFormulas.size());

        educationLevelFormulas.put("초등학교", elementaryFormulas);
        educationLevelFormulas.put("중학교", middleFormulas);
        educationLevelFormulas.put("고등학교", highFormulas);

        userData.put("educationLevelStats", educationLevelStats);
        userData.put("educationLevelFormulas", educationLevelFormulas);
        userData.put("totalByLevel", Map.of(
                "초등학교", totalElementary,
                "중학교", totalMiddle,
                "고등학교", totalHigh
        ));

        // 발견한 원소 분석
        Set<String> elements = new HashSet<>();
        for (String formula : discoveredFormulas) {
            Matcher matcher = Pattern.compile("[A-Z][a-z]?").matcher(formula);
            while (matcher.find()) {
                elements.add(matcher.group());
            }
        }

        userData.put("discoveredElements", elements);
        userData.put("totalElements", elements.size());

        // 획득한 칭호 정보
        List<String> titles = userTitles.stream()
                .map(ut -> ut.getTitle().getTitleName())
                .collect(Collectors.toList());

        userData.put("acquiredTitles", titles);
        userData.put("totalTitles", titles.size());

        // 최근 활동 분석
        searchHistories.sort(Comparator.comparing(FormulaSearchHistory::getId).reversed());
        List<String> recentDiscoveries = searchHistories.stream()
                .limit(5)
                .map(FormulaSearchHistory::getMatchedFormula)
                .collect(Collectors.toList());

        userData.put("recentDiscoveries", recentDiscoveries);

        return userData;
    }

    private String generateReportWithAI(Map<String, Object> userData) {
        // API 요청을 위한 프롬프트 생성
        String prompt = createReportPrompt(userData);

        // API 호출
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 화학 교육 전문가로, 사용자의 화학물질 발견 기록을 분석하여 객관적이고 교육적인 보고서를 작성합니다. 응답은 반드시 마크다운 형식으로 작성하며, 보고서 형태를 유지합니다."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // API 응답 처리
        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
        String markdownContent = extractResponseContent(response);

        // 반환 전에 마크다운 형식이 올바른지 확인
        if (!markdownContent.startsWith("#")) {
            markdownContent = "# " + userData.get("username") + "님의 화학 활동 분석 보고서\n\n" + markdownContent;
        }

        return markdownContent;
    }

    /**
     * 보고서 생성을 위한 프롬프트 작성
     */
    private String createReportPrompt(Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String organization = (String) userData.get("organization");
        List<String> discoveredFormulas = (List<String>) userData.get("discoveredFormulas");
        Map<String, Long> educationLevelStats = (Map<String, Long>) userData.get("educationLevelStats");
        Map<String, Object> totalByLevel = (Map<String, Object>) userData.get("totalByLevel");
        Set<String> discoveredElements = (Set<String>) userData.get("discoveredElements");
        List<String> acquiredTitles = (List<String>) userData.get("acquiredTitles");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String today = LocalDateTime.now().format(formatter);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("## 사용자 화학 활동 분석 보고서 작성 요청\n\n");
        promptBuilder.append("다음 정보를 바탕으로 사용자의 화학 활동에 대한 분석 보고서를 작성해주세요.\n\n");

        promptBuilder.append("### 기본 정보\n");
        promptBuilder.append("- 이름: ").append(username).append("\n");
        promptBuilder.append("- 소속: ").append(organization).append("\n");
        promptBuilder.append("- 보고서 작성일: ").append(today).append("\n\n");

        promptBuilder.append("### 화학물질 발견 현황\n");
        promptBuilder.append("- 총 발견 화학물질 수: ").append(discoveredFormulas.size()).append("\n");
        promptBuilder.append("- 발견한 화학물질 목록: ").append(String.join(", ", discoveredFormulas)).append("\n\n");

        promptBuilder.append("### 교육 수준별 발견 현황\n");
        promptBuilder.append("- 초등학교 수준: ").append(educationLevelStats.get("초등학교")).append("/")
                .append(totalByLevel.get("초등학교")).append("\n");
        promptBuilder.append("- 중학교 수준: ").append(educationLevelStats.get("중학교")).append("/")
                .append(totalByLevel.get("중학교")).append("\n");
        promptBuilder.append("- 고등학교 수준: ").append(educationLevelStats.get("고등학교")).append("/")
                .append(totalByLevel.get("고등학교")).append("\n\n");

        promptBuilder.append("### 발견한 원소 분석\n");
        promptBuilder.append("- 발견한 원소 수: ").append(discoveredElements.size()).append("\n");
        promptBuilder.append("- 발견한 원소 목록: ").append(String.join(", ", discoveredElements)).append("\n\n");

        promptBuilder.append("### 획득한 칭호\n");
        promptBuilder.append("- 총 획득 칭호 수: ").append(acquiredTitles.size()).append("\n");
        promptBuilder.append("- 획득한 칭호 목록: ").append(String.join(", ", acquiredTitles)).append("\n\n");

        promptBuilder.append("### 보고서 요청 사항\n");
        promptBuilder.append("1. 사용자의 화학 탐구 수준과 진행 상황에 대한 전반적인 평가\n");
        promptBuilder.append("2. 교육 수준별 발견 현황 분석 및 제안\n");
        promptBuilder.append("3. 원소 발견 패턴 분석 및 화학적 관심사 추론\n");
        promptBuilder.append("4. 향후 화학 탐구 방향 제안 (아직 발견하지 않은 중요 화학물질 추천)\n");
        promptBuilder.append("5. 전체적인 학습 성취도 평가 및 격려 메시지\n\n");

        promptBuilder.append("보고서는 Markdown 형식으로 작성해주세요. 학생의 성취를 격려하고, 교육적인 내용을 포함해야 합니다. ");
        promptBuilder.append("화학 분야에 대한 흥미를 높일 수 있는 내용도 포함해주세요.\n\n");

        return promptBuilder.toString();
    }

    /**
     * API 응답에서 컨텐츠 추출
     */
    private String extractResponseContent(Map<String, Object> response) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}