package kr.co.leaf2u_api.openai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String API_KEY;

    @Value("${openai.model}")
    private String MODEL;

    @Value("${openai.api.url}")
    private String API_URL;

    @Autowired
    private RestTemplate restTemplate;

    public String sendImageToGPT(MultipartFile file, String systemPrompt, String userPrompt) {
        try {
            byte[] imageBytes = file.getBytes();

            // Base64로 변환
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // JSON 요청 본문 생성
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL);
            requestBody.put("max_tokens", 50);

            // system 메시지 추가
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);

            // user 메시지 생성
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            // user의 content 배열 생성
            JSONArray userContentArray = new JSONArray();

            // 텍스트 메시지 추가
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", userPrompt);
            userContentArray.add(textContent);

            // 이미지 메시지 추가
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");

            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
            imageContent.put("image_url", imageUrl);

            userContentArray.add(imageContent);

            // userMessage에 content 추가
            userMessage.put("content", userContentArray);

            // messages 배열 생성
            JSONArray messages = new JSONArray();
            messages.add(systemMessage);
            messages.add(userMessage);

            // requestBody에 messages 추가
            requestBody.put("messages", messages);

            // JSON 문자열로 변환
            String requestBodyString = requestBody.toJSONString();

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, headers);

            // OpenAI API 요청 보내기
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

            // 응답 파싱
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>)choices.get(0).get("message");
            String result = (String)message.get("content");

            return result;
        } catch (IOException e) {
            throw new RuntimeException("이미지 처리 중 오류 발생", e);
        }
    }


    // OpenAI API를 사용하여 퀴즈 생성
    public Map<String, Object> createQuiz(String title, String content) {

        String prompt = "다음 뉴스 기사를 읽고, 기사 내용 중 환경 또는 기후와 관련된 OX 문제를 JSON 형식으로 만들어줘.\n\n"
                + "기사 제목: " + title + "\n"
                + "기사 내용: " + content + "\n\n"
                + "반드시 아래 JSON 형식으로 출력해\n"
                + "{\"quiz\": \"OX 문제 내용\", \"answer\": \"O 또는 X\", \"section\": \"문제를 추출한 문단\"}\n"
                + "문제의 답이 무조건 'O'가 되지 않도록 해."
                + "추가 설명 없이 반드시 JSON 형식만 반환해."
                + "```json``` 이라는 글자는 제외해.";

        // OpenAI API 요청 데이터
        Map<String, Object> request = Map.of(
                "model", MODEL,
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 OX 퀴즈를 생성하는 AI야. 주어진 뉴스 기사를 기반으로 OX 문제를 만들고, 정답을 반드시 포함해야 해. 퀴즈 내용은 기사 내용 content를 바탕으로 만들어주고, 난이도는 초등학생도 쉽게 풀 정도로 쉽게 해줘. 문제를 추출한 문단은 최소 3줄로 구성해줘. 반드시 JSON 형식으로 반환해."),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 500
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);

        // OpenAI API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, requestEntity, Map.class);

        Map<String, Object> result = new HashMap<>();

        // 응답 데이터에서 문제 추출
        try {
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String responseContent = (String) message.get("content");
                    System.out.println("=====================> " + responseContent);

                    // JSON 형식이 맞는지 확인 후 파싱
                    if (!responseContent.trim().startsWith("{")) {
                        return Map.of("error", "OpenAI 응답이 JSON 형식이 아닙니다.");
                    }

                    // OpenAI 응답을 JSON으로 변환
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(responseContent);
                    result = (Map<String, Object>) jsonObject;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}