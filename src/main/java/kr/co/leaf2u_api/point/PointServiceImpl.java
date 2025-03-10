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
        // 오늘 날짜의 시작과 끝
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 기존 출석체크 여부 확인
        Optional<Point> existingCheckIn = pointRepository.findFirstByMemberAndEarnDateBetween(member, startOfDay, endOfDay);

        if (existingCheckIn.isPresent()) {
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
}
