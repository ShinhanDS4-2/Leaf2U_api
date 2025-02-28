package kr.co.leaf2u_api.openai;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

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

    public String sendImageToGPT(MultipartFile file, String prompt) {
        try {
            byte[] imageBytes = file.getBytes();

            // 이미지 크기 줄이기 (가로 300px로 리사이징, JPEG 품질 0.75로 설정)
            byte[] resizedImageBytes = resizeImage(imageBytes, 300, 0.75f);

            // Base64로 변환
            String base64Image = Base64.getEncoder().encodeToString(resizedImageBytes);

            // JSON 요청 본문 생성
            String requestBody = """
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "system",
                            "content": "You are an AI assistant that identifies tumblers in images."
                        },
                        {
                            "role": "user",
                            "content": [
                                { "type": "text", "text": "%s" },
                                { "type": "image_url", "image_url": { "url": "data:image/jpeg;base64,%s" } }
                            ]
                        }
                    ],
                    "max_tokens": 10
                }
            """.formatted(MODEL, prompt, base64Image);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // OpenAI API 요청 보내기
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

            // 응답 파싱
            return response.getBody().toString();
        } catch (IOException e) {
            throw new RuntimeException("이미지 처리 중 오류 발생", e);
        }
    }

    /**
     * 이미지 리사이징 및 압축 메서드
     * @param imageBytes 원본 이미지 바이트 배열
     * @param width 목표 가로 크기 (세로 비율 유지)
     * @param quality 이미지 품질 (0.0 ~ 1.0)
     * @return 리사이징된 이미지 바이트 배열
     * @throws IOException
     */
    private byte[] resizeImage(byte[] imageBytes, int width, float quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new java.io.ByteArrayInputStream(imageBytes))
                .size(width, width) // 가로를 300px로 설정, 세로는 자동 조정
                .outputQuality(quality) // 이미지 품질 설정 (1.0 = 최고 품질)
                .outputFormat("jpeg") // 포맷을 JPEG로 변환
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }
}