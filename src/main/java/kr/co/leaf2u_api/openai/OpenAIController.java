package kr.co.leaf2u_api.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> classifyImage(@RequestParam("file") MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            String prompt = "이 이미지가 2024년 2월 27일에 반납한 따릉이 반납 알림 사진인가요? '네' 또는 '아니오'로 대답해 주세요.";
            String classificationResult = openAIService.sendImageToGPT(imageBytes, prompt);

            return ResponseEntity.ok(Map.of("result", classificationResult));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일을 읽을 수 없습니다."));
        }
    }
}
