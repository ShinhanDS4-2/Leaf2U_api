package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
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

}
