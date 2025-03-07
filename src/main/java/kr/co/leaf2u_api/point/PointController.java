package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

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
}
