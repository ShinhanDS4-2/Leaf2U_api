package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public boolean checkIn(Member member) {
        // 오늘 날짜의 시작과 끝
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        //기존 출석체크 여부 확인
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
        return true;
    }
}
