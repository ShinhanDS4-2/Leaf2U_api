package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final OpenAIService openAIService;

    @PostMapping("/checkin")
    public ResponseEntity<Map<String, Object>> checkIn(@RequestParam("memberIdx") Long memberIdx) {

        Member member = new Member();
        member.setIdx(memberIdx);

        boolean success = pointService.checkIn(member);
        if (!success) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 출석체크 완료"));
        }

        return ResponseEntity.ok(Map.of("message", "출석체크 완료! 10P 적립"));
    }

    @PostMapping("/pedometer")
    public ResponseEntity<Map<String, Object>> pedometerCheck(@RequestParam("memberIdx") Long memberIdx, @RequestParam("file") MultipartFile file) {
        try {
            // OpenAI를 활용하여 걸음 수 확인
            String systemPrompt = "당신은 만보기 데이터를 확인하는 AI 비서입니다.";
            String userPrompt = "이 이미지에서 걸음 수를 숫자로만 추출해 주세요.";
            String stepCountStr = openAIService.sendImageToGPT(file, systemPrompt, userPrompt);

            int stepCount;
            try {
                stepCount = Integer.parseInt(stepCountStr.replaceAll("[^0-9]", "")); // 숫자만 추출
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "걸음 수를 인식할 수 없습니다."));
            }

            // 1000걸음당 10포인트 적립
            int points = (stepCount / 1000) * 10;
            if (points == 0) {
                return ResponseEntity.ok(Map.of("message", "걸음 수 부족. 포인트가 적립되지 않았습니다."));
            }

            Member member = new Member();
            member.setIdx(memberIdx);

            // 포인트 적립
            pointService.addPedometerPoints(member, points);

            return ResponseEntity.ok(Map.of("message", "만보기 포인트 적립 완료!", "earnedPoints", points));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "이미지를 처리할 수 없습니다."));
        }
    }
}
