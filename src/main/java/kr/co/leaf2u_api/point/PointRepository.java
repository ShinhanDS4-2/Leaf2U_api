package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findFirstByMemberAndEarnDateBetween(Member member, LocalDateTime start, LocalDateTime end);
    List<Point> findByMember(Member member);

    @Query("""
        SELECT COALESCE(SUM(p.earnPoint), 0) - COALESCE(SUM(p.usePoint), 0)
        FROM Point p
        WHERE p.member = :member
    """)
    BigDecimal getTotalPoint(@Param("member") Member member);
}
