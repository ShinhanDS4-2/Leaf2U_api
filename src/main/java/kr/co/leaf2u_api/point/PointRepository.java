package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findFirstByMemberAndEarnDateBetween(Member member, LocalDateTime start, LocalDateTime end);
}
