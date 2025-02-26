package kr.co.leaf2u_api.challenge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class ChallengeServiceImpl implements ChallengeService {

    private final OpenAiconfig openAiConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ChallengeResponseDto analyzeImage(ChallengeRequestDto requestDto) {
        String openAiApiKey = openAiConfig.getKey();
        MultipartFile image = requestDto.getImage();

        try {
            // 1. 이미지를 Base64 인코딩
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

            // 2. OpenAI API 요청 데이터 생성 (Map 사용)
            Map<String, Object> messageContent = new HashMap<>();
            messageContent.put("type", "image");
            messageContent.put("image", base64Image);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", new Object[]{messageContent});

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "get-4-vision-preview");
            requestBody.put("messages", new Object[]{userMessage});
            requestBody.put("max_tokens", 100);

            // 3. HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openAiApiKey);

            // 4. HTTP 요청 엔터티 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 5. OpenAI API 호출
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // 6. 응답 데이터 처리
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());

            // "choices" → 0번째 요소 → "message" → "content"에서 결과 텍스트 가져오기
            String result = jsonNode.path("choices").get(0).path("message").path("content").asText();

            // 7. 응답 DTO 생성 및 반환
            ChallengeResponseDto responseDto = new ChallengeResponseDto();
            responseDto.setResult(result);
            return responseDto;

        } catch (Exception e) {
            throw new RuntimeException("이미지 분석 중 오류 발생", e);
        }

    }

}
