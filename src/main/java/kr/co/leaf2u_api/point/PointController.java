package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.config.TokenContext;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.openai.OpenAIService;
import kr.co.leaf2u_api.topic.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final TopicService topicService;
    private  final OpenAIService openAIService;

    // 출석체크 (하루 1회)
    @PostMapping("/checkin")
    public ResponseEntity<Map<String, Object>> checkIn() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        boolean success = pointService.checkIn(member);
        if (!success) {
            return ResponseEntity.ok(Map.of("success", false, "message", "이미 출석체크 완료"));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "출석체크 완료! 10P 적립"));
    }

    /**
     * 오늘 참여 확인
     * @param activityType
     * @return
     */
    @PostMapping("/check/today")
    public ResponseEntity<Boolean> checkToday(@RequestParam("activityType") char activityType) {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        boolean alreadyChecked = pointService.checkTodayActivity(member, activityType); // 'S' = 만보기, 'Q' =  퀴즈

        return ResponseEntity.ok(alreadyChecked);
    }

    /**
     * 만보기
     * @param file
     * @return
     */
    @PostMapping("/pedometer")
    public ResponseEntity<Map<String, Object>> pedometerCheck(@RequestParam("file") MultipartFile file) {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        // OpenAI를 활용하여 걸음 수 확인
        String systemPrompt = "당신은 만보기 데이터를 확인하는 AI 비서입니다.";
        String userPrompt = "이 이미지에서 걸음 수를 숫자로만 추출해 주세요.";
        String stepCountStr = openAIService.sendImageToGPT(file, systemPrompt, userPrompt);

        int stepCount = Integer.parseInt(stepCountStr.replaceAll("[^0-9]", ""));
        System.out.println("=================> " + stepCount);
        if (stepCount < 1000) {
            return ResponseEntity.ok(Map.of("message", "걸음 수 부족. 포인트가 적립되지 않았습니다."));
        }

        // 1000걸음당 10포인트 적립
        int points = (stepCount / 100) / 10 * 10;

        pointService.Pedometer(member, points);

        return ResponseEntity.ok(Map.of("success", true, "message", "만보기 포인트 적립 완료!", "point", points));
    }

    // 퀴즈 (하루 1회)
    @GetMapping("/quiz")
    public ResponseEntity<Map<String, Object>> getQuiz() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        List<Map<String, Object>> newsList = topicService.getNews();
        if (newsList == null || newsList.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "퀴즈를 생성할 뉴스가 없습니다."));
        }

        Map<String, Object> selectedNews = newsList.get(new Random().nextInt(newsList.size()));
        Map<String, Object> quizQuestion = topicService.createQuiz(
                (String) selectedNews.get("title"),
                (String) selectedNews.get("description")
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "question", quizQuestion,
                "newsId", selectedNews.get("url")
        ));
    }

    // 퀴즈 힌트 (5P 적립)
    @PostMapping("/quiz/hint")
    public ResponseEntity<Boolean> getQuizHint() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        boolean alreadyChecked = pointService.checkTodayActivity(member, 'H');
        if (alreadyChecked) {
            return ResponseEntity.ok(false);
        } else {
            pointService.QuizHint(member);
            return ResponseEntity.ok(true);
        }
    }

    // 퀴즈 정답
    @PostMapping("/quiz/answer")
    public ResponseEntity<Map<String, Object>> submitQuizAnswer() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        boolean alreadyChecked = pointService.checkTodayActivity(member, 'Q');
        if (alreadyChecked) {
            return ResponseEntity.ok(Map.of("success", false, "message", "오늘은 이미 퀴즈에 참여하였습니다."));
        }

        pointService.QuizCorrect(member);

        return ResponseEntity.ok(Map.of("success", true, "message", "정답! 10P 적립"));
    }

    // 포인트 총합 조회
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalPoints() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        BigDecimal totalPoints = pointService.getTotalPoints(member);

        return ResponseEntity.ok(Map.of("totalPoints", totalPoints));
    }
}
