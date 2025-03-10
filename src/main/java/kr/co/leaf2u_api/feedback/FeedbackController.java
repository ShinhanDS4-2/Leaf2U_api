package kr.co.leaf2u_api.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, Object>> getFeedback(@RequestBody FeedbackDTO request) {
        try {
            // 사용자의 챌린지 수행 횟수 및 평균 횟수 조회
            int userChallengeCount = feedbackService.getUserChallengeCount(request.getAccountIdx());
            int averageChallengeCount = feedbackService.getAverageChallengeCount();

            System.out.println("사용자 챌린지 횟수=" + userChallengeCount + ", 평균 챌린지 횟수=" + averageChallengeCount);

            // 프롬프트 생성
            String systemPrompt = "당신은 챌린지 수행 데이터를 분석하여 피드백을 제공하는 AI 비서입니다. 평균과 사용자의 수행한 횟수는 꼭 비교하며 사용자의 챌린지 횟수를 기준으로 격려와 조언을 제공합니다. 챌린지라는 단어를 꼭 넣고 조언은 두 문장으로 끝내주세요";
            String userPrompt = String.format(
                    "전체 회원들의 챌린지 시행 평균은 %d회 정도 수행합니다. 현재 사용자는 챌린지 몇 %d회를 수행했습니다. " + "사용자가 동기부여를 가질 수 있도록 적절한 피드백을 제공하세요.",
                    Integer.parseInt(String.valueOf(averageChallengeCount)),
                    Integer.parseInt(String.valueOf(userChallengeCount))
            );

            // OpenAI API 호출 (FeedbackService에서 처리)
            String feedback = feedbackService.generateFeedback(systemPrompt, userPrompt);
            return ResponseEntity.ok(Map.of("feedback", feedback));
        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "피드백 생성 중 오류 발생"));
        }
    }
}
