package kr.co.leaf2u_api.notice;

import kr.co.leaf2u_api.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 사용자 별 알림 리스트 (오늘)
     * @param memberIdx
     * @return
     */
    @Query("""
        SELECT n 
        FROM Notice n 
        WHERE n.member.idx = :memberIdx 
        AND FUNCTION('DATE', n.createDate) = CURRENT_DATE
        ORDER BY n.createDate DESC
    """)
    List<Notice> getNoticeListWithToday(@Param("memberIdx") Long memberIdx);

    /**
     * 사용자 별 알림 리스트 (이전)
     * @param memberIdx
     * @return
     */
    @Query("""
        SELECT n 
        FROM Notice n 
        WHERE n.member.idx = :memberIdx 
        AND FUNCTION('DATE', n.createDate) != CURRENT_DATE
        ORDER BY n.createDate DESC
        LIMIT 5
    """)
    List<Notice> getNoticeListWithPrev(@Param("memberIdx") Long memberIdx);

    /**
     * 오늘 알림 유무 체크
     * @param memberIdx
     * @return
     */
    @Query("""
        SELECT COUNT(n) 
        FROM Notice n 
        WHERE n.member.idx = :memberIdx 
        AND n.category = 'D'
        AND FUNCTION('DATE', n.createDate) = FUNCTION('CURRENT_DATE')
        ORDER BY n.createDate DESC
    """)
    Long checkDailyNotice(@Param("memberIdx") Long memberIdx);
}
