package kr.co.leaf2u_api.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @PostMapping("/image/tumblr")
    public ResponseEntity<Map<String, Object>> checkTumblr(@RequestParam("file") MultipartFile file) {
        try {
            String systemPrompt = "당신은 이미지 속 텀블러를 식별하는 AI 비서입니다.";
            String userPrompt = "이 이미지가 텀블러 사진인가요? 'Yes' 또는 'No'로 대답해 주세요.";
            String result = openAIService.sendImageToGPT(file, systemPrompt, userPrompt);

            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "파일을 읽을 수 없습니다."));
        }
    }

    @PostMapping("/image/bicycle")
    public ResponseEntity<Map<String, Object>> checkBicycle(@RequestParam("file") MultipartFile file) {
        try {
            String systemPrompt = "당신은 서울시 공공자전거 따릉이 반납완료 스크린샷을 확인하는 AI 비서입니다. 이미지에서 반납일시 추출하는 데 중점을 둡니다.";
            String userPrompt = "추가적인 코멘트 없이 이 이미지에서 반납일시만 추출해 주세요. 전자영수증이면 날짜를 출력하지 말아주세요. 'MM-dd' 형태로 출력해 주세요.";
            String result = openAIService.sendImageToGPT(file, systemPrompt, userPrompt);

            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일을 읽을 수 없습니다."));
        }
    }

    @PostMapping("/image/receipt")
    public ResponseEntity<Map<String, Object>> checkReceipt(@RequestParam("file") MultipartFile file) {
        try {
            String systemPrompt = "당신은 전자 영수증 스크린샷을 확인하는 AI 비서입니다. 종이로 된 영수증 이미지를 받을 경우 대답은 절대 'No'입니다. 이미지에서 영수증 발급 날짜 추출하는 데 중점을 둡니다.";
            String userPrompt = "따릉이 반납 사진은 무조건 'No' 입니다. 해당 이미지가 전자영수증이면 'Yes', 아니면 'No'로만 답해주고, 만약 Yes일 경우 해당 이미지에서 영수증 발급 날짜도 더해서 출력해 주세요. 날짜 형식은 yyyy-mm-dd(시간까지 있을 경우 yyyy-mm-dd HH:mm:ss)";
            String result = openAIService.sendImageToGPT(file, systemPrompt, userPrompt);

            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일을 읽을 수 없습니다."));
        }
    }
}
