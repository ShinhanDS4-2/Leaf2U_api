package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    // 카테고리별 Tip 가져오기
    @GetMapping("/tips")
    public List<EcoTips> getTipsByCategory() {
        return topicService.getEcoTips();
    }

    // 새로운 Tip 추가하기
    @PostMapping
    public EcoTips createEcoTips(@RequestBody EcoTips ecoTips) {
        return topicService.saveEcoTips(ecoTips);
    }

    /**
     * 뉴스 기사 api
     */
    @GetMapping("/news")
    public ResponseEntity<List<Map<String, Object>>> getNews() {

        List<Map<String, Object>> response = topicService.getNews();

        return ResponseEntity.ok(response);
    }

    /**
     * 미세먼지
     */
    @GetMapping("/finedust")
    public ResponseEntity<Map<String, Object>> getFineDust() {
        Map<String, Object> dustInfo = topicService.getFineDustInfo();

        if (dustInfo.containsKey("error")) {
            return ResponseEntity.status(500).body(dustInfo);
        }

        return ResponseEntity.ok(dustInfo);
    }

}
