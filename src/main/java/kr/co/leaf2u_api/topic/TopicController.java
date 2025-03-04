package kr.co.leaf2u_api.topic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leaf2u_api.entity.EcoTips;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    // 카테고리별 Tip 가져오기
    @GetMapping("/{category}")
    public List<EcoTips> getTipsByCategory(@PathVariable char category) {
        return topicService.getEcoTips(category);
    }

    // 새로운 Tip 추가하기
    @PostMapping
    public EcoTips createTip(@RequestBody EcoTips tip) {
        return topicService.saveEcoTips(tip);
    }

    @GetMapping("/news")
    public ResponseEntity<Map<String, Object>> getNews(@RequestParam("q") String keyword) {

        Map<String, Object> response = topicService.getNews(keyword);

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/news/quiz")
    public ResponseEntity<Map<String, Object>> getQuiz(@RequestParam("q") String keyword) {
        Map<String, Object> newsData = topicService.getNews(keyword);

        if (newsData.containsKey("error")) {
            return ResponseEntity.status(404).body(newsData);
        }

        // 뉴스 제목과 내용을 바탕으로 퀴즈 생성
        String quizJson = topicService.createQuiz(
                (String) newsData.get("title"),
                (String) newsData.get("description")
        );

        try {
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> quizData = objectMapper.readValue(quizJson, new TypeReference<Map<String, Object>>() {
            });

            // 순서를 유지하는 LinkedHashMap 사용
            Map<String, Object> orderedResponse = new LinkedHashMap<>();
            orderedResponse.put("title", newsData.get("title"));
            orderedResponse.put("quiz", quizData.get("quiz"));
            orderedResponse.put("answer", quizData.get("answer"));

            return ResponseEntity.ok(orderedResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "퀴즈를 생성하는 중 오류 발생",
                    "details", e.getMessage()
            ));
        }
    }
}
