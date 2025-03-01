package kr.co.leaf2u_api.openai;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
}