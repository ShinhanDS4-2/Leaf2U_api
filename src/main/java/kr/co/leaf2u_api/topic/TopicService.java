package kr.co.leaf2u_api.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopicService {

    @Value("${newsapi.api.key}")
    private String NEWS_API_KEY;

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Value("${openai.model}")
    private String MODEL;

    @Value("${openai.api.url}")
    private String API_URL;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> getNews(String keyword) {
        String url = "https://newsapi.org/v2/everything?q=" + keyword + "+&language=ko&sortBy=relevancy&apiKey=" + NEWS_API_KEY;


        // 뉴스 API 호출
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");

        if (articles == null || articles.isEmpty()) {
            return Map.of("error", "No articles found");
        }

        System.out.println(articles);

        // 첫 번째 기사만 선택
        Map<String, Object> article = articles.get(0);

        // 날짜를 "YYYY-MM-DD" 형식으로 반환
        String publishedAt = (String) article.get("publishedAt");
        String formattedDate = (publishedAt != null && publishedAt.length() >= 10) ? publishedAt.substring(0, 10) : "N/A";



        return Map.of(
                "title", (String) article.get("title"),
                "description", (String) article.get("description"),
                "url", (String) article.get("url"),
                "date", formattedDate
        );
    }

    // OpenAI API를 사용하여 퀴즈 생성
    public String createQuiz(String title, String content) {

        String prompt = "다음 뉴스 기사를 읽고 관련된 OX 문제를 JSON 형식으로 만들어줘.\n\n"
                + "기사 제목: " + title + "\n"
                + "기사 내용: " + content + "\n\n"
                + "📌 반드시 아래 JSON 형식으로 출력해 (한 줄로 출력할 것):\n"
                + "{\"quiz\": \"OX 문제 내용\", \"answer\": \"O 또는 X\"}\n"
                + "문제의 답이 무조건 'O'가 되지 않도록 해."
                + "추가 설명 없이 오직 JSON 형식만 반환해.";

        // OpenAI API 요청 데이터
        Map<String, Object> request = Map.of(
                "model", MODEL,
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 OX 퀴즈를 생성하는 AI야. 주어진 뉴스 기사를 기반으로 OX 문제를 만들고, 정답을 반드시 포함해야 해."),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 200
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);

        // OpenAI API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, requestEntity, Map.class);

        // 응답 데이터에서 문제 추출
        try {
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("choices")) {
                var choices = (List<Map<String, Object>>) body.get("choices");
                if (!choices.isEmpty()) {
                    var message = (Map<String, Object>) choices.get(0).get("message");
                    String responseContent = (String) message.get("content");

                    // JSON 형식이 맞는지 확인 후 파싱
                    if (!responseContent.trim().startsWith("{")) {
                        return "{\"error\": \"OpenAI 응답이 JSON 형식이 아닙니다.\", \"response\": \"" + responseContent + "\"}";
                    }

                    // OpenAI 응답을 JSON으로 변환
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> quizData = objectMapper.readValue(responseContent, new TypeReference<Map<String, String>>() {});

                    return objectMapper.writeValueAsString(quizData);
                }
            }
        } catch (Exception e) {
            return "퀴즈를 생성할 수 없습니다.";
        }

        return "퀴즈를 생성할 수 없습니다.";
    }

}