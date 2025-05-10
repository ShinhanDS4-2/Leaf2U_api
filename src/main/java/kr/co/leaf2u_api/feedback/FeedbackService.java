package kr.co.leaf2u_api.feedback;

import kr.co.leaf2u_api.openai.OpenAIService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public class FeedbackService {

    @Value("${openai.api.key}")
    private String API_KEY;

    @Value("${openai.model}")
    private String MODEL;

    @Value("${openai.api.url}")
    private String API_URL;
    @Autowired
    private RestTemplate restTemplate;

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // 1. 사용자의 챌린지 수행 횟수 업데이트
    public void updateUserChallenge(Long accountIdx) {
        feedbackRepository.updateUserChallenge(accountIdx);
    }

    // 2. 특정 사용자의 챌린지 수행 횟수 조회
    public int getUserChallengeCount(Long accountIdx) {
        return feedbackRepository.getUserChallengeCount(accountIdx);
    }

    // 3. 전체 평균 챌린지 수행 횟수 조회
    public int getAverageChallengeCount() {
        int avg = feedbackRepository.getAverageChallengeCount();
        return avg;
    }

    // OpenAI API 호출 메서드
    public String generateFeedback(String systemPrompt, String userPrompt) {
        try {
            // JSON 요청 본문 생성
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL);
            requestBody.put("max_tokens", 150);

            // System 메시지 설정
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);

            // User 메시지 설정
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);

            // 메시지 배열 생성
            JSONArray messages = new JSONArray();
            messages.add(systemMessage);
            messages.add(userMessage);

            // requestBody에 messages 추가
            requestBody.put("messages", messages);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toJSONString(), headers);

            // OpenAI API 요청 보내기
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

            // 응답 파싱
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
            return "피드백을 생성할 수 없습니다.";
        }
    }
}