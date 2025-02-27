package kr.co.leaf2u_api.ai.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    @Value("${openai.model}")
    private String MODEL;

    @Value("${openai.api.url}")
    private String APIURL;

    @Value("${openai.api.key}")
    private String OPENAIKEY;

//    private final RestTemplate restTemplate;

    @Override
    public ChallengeResponseDto analyzeImage(MultipartFile image) {

        try {
            // 이미지 파일을 Base64로 변환
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            // OpenAI API 요청 객체 생성 (Base64 이미지 전송)
            ChallengeRequestDto request = new ChallengeRequestDto(MODEL, base64Image);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAIKEY); // API 키
            // Http 요청 엔티티 생성
            HttpEntity<ChallengeRequestDto> requestEntity = new HttpEntity<>(request, headers);


            // OpenAI API 호출
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForObject(APIURL, request, ChallengeResponseDto.class);

        } catch (Exception e) {
            throw new RuntimeException("이미지 분석 중 오류 발생", e);
        }
    }

}
