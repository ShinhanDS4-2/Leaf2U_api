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
        if (checkTodayActivity(member, 'A')) return false; // ✅ 출석체크 1회 제한

        Point point = Point.builder()
                .member(member)
                .earnType('A')
                .earnPoint(BigDecimal.valueOf(10))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .build();

        pointRepository.save(point);

        noticeService.registNotice(Map.of(
                "memberIdx", member.getIdx(),
                "title", "포인트 획득",
                "content", "출석체크 10P 적립!",
                "category", "P"
        ));

        return true;
    }

    @Transactional
    @Override
    public void Pedometer(Member member, int points) {
        if (checkTodayActivity(member, 'S')) return; // 만보기 1회 제한

        Point point = Point.builder()
                .member(member)
                .earnType('S')
                .earnPoint(BigDecimal.valueOf(points))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .build();

        pointRepository.save(point);

        noticeService.registNotice(Map.of(
                "memberIdx", member.getIdx(),
                "title", "포인트 적립",
                "content", "만보기 " + points + "P 적립!",
                "category", "P"
        ));
    }

    @Transactional
    @Override
    public void QuizHint(Member member) {
        Point point = Point.builder()
                .member(member)
                .earnType('H')
                .earnPoint(BigDecimal.valueOf(5))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .build();

        pointRepository.save(point);

        noticeService.registNotice(Map.of(
                "memberIdx", member.getIdx(),
                "title", "포인트 적립",
                "content", "퀴즈 힌트 5P 적립!",
                "category", "P"
        ));
    }

    @Transactional
    @Override
    public void QuizCorrect(Member member) {
        if (checkTodayActivity(member, 'Q')) return; // 퀴즈 정답 1회 제한

        Point point = Point.builder()
                .member(member)
                .earnType('Q')
                .earnPoint(BigDecimal.valueOf(10))
                .usePoint(BigDecimal.ZERO)
                .earnDate(LocalDateTime.now())
                .build();

        pointRepository.save(point);

        noticeService.registNotice(Map.of(
                "memberIdx", member.getIdx(),
                "title", "포인트 적립",
                "content", "퀴즈 정답 10P 적립!",
                "category", "P"
        ));
    }

    @Transactional
    @Override
    public boolean checkTodayActivity(Member member, char activityType) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        Optional<Point> existingActivity = pointRepository
                .findFirstByMemberAndEarnTypeAndEarnDateBetween(member, activityType, startOfDay, endOfDay);

        return existingActivity.isPresent();  // 이미 참여했으면 true 반환
    }



    @Transactional
    @Override
    public BigDecimal getTotalPoints(Member member) {
        try {
            return pointRepository.getTotalPoint(member);
        } catch (Exception e) {
            System.out.println("Error calculating total points: " + e.getMessage());
            throw new RuntimeException("포인트 계산 중 오류가 발생했습니다.");
        }
    }
}
