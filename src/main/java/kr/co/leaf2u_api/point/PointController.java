package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.config.TokenContext;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.topic.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 만보기 (하루 1회)
    @PostMapping("/pedometer")
    public ResponseEntity<Map<String, Object>> pedometerCheck() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        boolean alreadyChecked = pointService.checkTodayActivity(member, 'S'); // 'S' = 만보기
        if (alreadyChecked) {
            return ResponseEntity.ok(Map.of("success", false, "message", "오늘은 이미 만보기 인증을 완료하였습니다."));
        }

        pointService.Pedometer(member, 10);

        return ResponseEntity.ok(Map.of("success", true, "message", "만보기 포인트 적립 완료!"));
    }

    // 퀴즈 (하루 1회)
    @GetMapping("/quiz")
    public ResponseEntity<Map<String, Object>> getQuiz() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        boolean alreadyChecked = pointService.checkTodayActivity(member, 'Q'); // 'Q' = 퀴즈
        if (alreadyChecked) {
            return ResponseEntity.ok(Map.of("success", false, "message", "오늘은 이미 퀴즈에 참여하였습니다."));
        }

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
    public ResponseEntity<Map<String, Object>> getQuizHint(@RequestBody Map<String, Object> request) {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        pointService.QuizHint(member);

        String newsUrl = (String) request.get("newsId");

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "힌트 제공! 5P 적립",
                "newsUrl", newsUrl
        ));
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
