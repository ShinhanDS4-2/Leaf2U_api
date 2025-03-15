package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Point;
import kr.co.leaf2u_api.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final NoticeService noticeService;

    @Transactional
    @Override
    public boolean checkIn(Member member) {
        if (member == null || member.getIdx() == null) {
            System.out.println("❌ [출석 체크 오류] member가 null이거나 memberIdx가 없습니다.");
            throw new IllegalArgumentException("멤버 정보가 올바르지 않습니다.");
        }

        System.out.println("🔎 [출석 체크 로직] memberIdx: " + member.getIdx());

        // 기존 출석체크 여부 확인
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 기존 출석체크 여부 확인
        Optional<Point> existingCheckIn = pointRepository.findFirstByMemberAndEarnDateBetween(member, startOfDay, endOfDay);

        if (existingCheckIn.isPresent()) {
            System.out.println("⚠️ 이미 출석 체크 완료된 사용자입니다.");
            return false; // 이미 출석체크 완료
        }

        // 포인트 적립
        Point point = Point.builder()
                .member(member)
                .earnType('A')
                .earnPoint(BigDecimal.valueOf(10))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .useDate(null)
                .build();

        pointRepository.save(point);

        // 출석체크 포인트 알림 insert
        Map<String, Object> noticeParam = new HashMap<>();
        noticeParam.put("memberIdx", member.getIdx());
        noticeParam.put("title", "포인트 획득");
        noticeParam.put("content", "출석체크 10P 획득!");
        noticeParam.put("category", "P");

        noticeService.registNotice(noticeParam);

        return true;
    }

    /**
     * 만보기
     * @param member
     * @param points
     */
    @Transactional
    @Override
    public void addPedometerPoints(Member member, int points) {
        Point point = Point.builder()
                .member(member)
                .earnType('S')
                .earnPoint(BigDecimal.valueOf(points))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .useDate(null)
                .build();

        pointRepository.save(point);

        // 포인트 적립 알림
        Map<String, Object> noticeParam = new HashMap<>();
        noticeParam.put("memberIdx", member.getIdx());
        noticeParam.put("title", "포인트 적립");
        noticeParam.put("content", "만보기 포인트 " + points + "P 적립!");
        noticeParam.put("category", "P");

        noticeService.registNotice(noticeParam);
    }
    @Transactional
    @Override
    public void addQuizHintPoint(Member member) {
        Point point = Point.builder()
                .member(member)
                .earnType('H') // 힌트 포인트
                .earnPoint(BigDecimal.valueOf(5))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .build();

        pointRepository.save(point);
    }

    @Transactional
    @Override
    public void addQuizCorrectPoint(Member member) {
        Point point = Point.builder()
                .member(member)
                .earnType('Q') // 퀴즈 정답 포인트
                .earnPoint(BigDecimal.valueOf(10))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .build();

        pointRepository.save(point);
    }

    /**
     * 총 포인트
     * @param member
     * @return
     */
    @Transactional
    @Override
    public BigDecimal getTotalPoints(Member member) {
        try {
            BigDecimal totalPoints = pointRepository.getTotalPoint(member);
            return totalPoints;
        } catch (Exception e) {
            // 예외 발생 시 로그로 확인
            System.out.println("Error calculating total points: " + e.getMessage());
            throw new RuntimeException("포인트 계산 중 오류가 발생했습니다.");
        }
    }
}
