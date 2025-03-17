package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.config.TokenContext;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.openai.OpenAIService;
import kr.co.leaf2u_api.topic.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final OpenAIService openAIService;
    private final TopicService topicService;

    /**
     * 출석체크
     */
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

    @PostMapping("/pedometer")
    public ResponseEntity<Map<String, Object>> pedometerCheck(@RequestParam("memberIdx") Long memberIdx,
                                                              @RequestParam("file") MultipartFile file) {
        try {
            Member member = new Member();
            member.setIdx(memberIdx);

            try {
                // OpenAI를 활용하여 걸음 수 확인
                String systemPrompt = "당신은 만보기 데이터를 확인하는 AI 비서입니다.";
                String userPrompt = "이 이미지에서 걸음 수를 숫자로만 추출해 주세요.";
                String stepCountStr = openAIService.sendImageToGPT(file, systemPrompt, userPrompt);

                int stepCount = Integer.parseInt(stepCountStr.replaceAll("[^0-9]", ""));

                // 1000걸음당 10포인트 적립
                int points = (stepCount / 1000) * 10;
                if (points == 0) {
                    return ResponseEntity.ok(Map.of("message", "걸음 수 부족. 포인트가 적립되지 않았습니다."));
                }

                pointService.addPedometerPoints(member, points);

                return ResponseEntity.ok(Map.of("message", "만보기 포인트 적립 완료!", "earnedPoints", points));

            } catch (IllegalStateException e) {
                return ResponseEntity.ok(Map.of("message", "이미 오늘 만보기 포인트 적립이 완료되었습니다."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "이미지를 처리할 수 없습니다."));
        }
    }
    /**
     * 퀴즈
     */
    @GetMapping("/quiz")
    public ResponseEntity<Map<String, Object>> getQuiz() {
        List<Map<String, Object>> newsList = topicService.getNews();

        if (newsList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "퀴즈를 생성할 뉴스가 없습니다."));
        }

        // 뉴스 3개 중 랜덤으로 하나 선택
        Map<String, Object> selectedNews = newsList.get(new Random().nextInt(newsList.size()));

        // 퀴즈 생성
        Map<String, Object> quizQuestion = topicService.createQuiz(
                (String) selectedNews.get("title"),
                (String) selectedNews.get("description")
        );

        return ResponseEntity.ok(Map.of(
                "question", quizQuestion,
                "newsId", selectedNews.get("url") // 기사 URL을 ID로 활용
        ));
    }

    /**
     * 퀴즈 힌트
     */
    @PostMapping("/quiz/hint")
    public ResponseEntity<Map<String, Object>> getQuizHint() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        // 포인트 적립
        pointService.addQuizHintPoint(member);

        return ResponseEntity.ok(Map.of(
                "message", "힌트 제공! 5P 적립"
        ));
    }



    @PostMapping("/quiz/answer")
    public ResponseEntity<Map<String, Object>> submitQuizAnswer(@RequestParam("answer") String answer) {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        try {
            if ("O".equalsIgnoreCase(answer)) {
                pointService.addQuizCorrectPoint(member);
                return ResponseEntity.ok(Map.of("message", "정답! 10P 적립"));
            } else {
                return ResponseEntity.ok(Map.of("message", "오답입니다. 다음 기회에 도전하세요!"));
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(Map.of("message", "이미 오늘 퀴즈 포인트 적립이 완료되었습니다."));
        }
    }
    /**
     * 포인트 총합
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalPoints() {
        Member member = new Member();
        member.setIdx(TokenContext.getMemberIdx());

        // 포인트 총합 계산
        BigDecimal totalPoints = pointService.getTotalPoints(member);

        return ResponseEntity.ok(Map.of(
                "totalPoints", totalPoints
        ));
    }
}
