package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findFirstByMemberAndEarnTypeAndEarnDateBetween(
            @Param("member") Member member,
            @Param("activityType") char activityType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );



    @Query("""
        SELECT COALESCE(SUM(p.earnPoint), 0) - COALESCE(SUM(p.usePoint), 0)
        FROM Point p
        WHERE p.member = :member
    """)
    BigDecimal getTotalPoint(@Param("member") Member member);
}
