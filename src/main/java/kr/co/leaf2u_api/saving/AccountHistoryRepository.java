package kr.co.leaf2u_api.saving;

import kr.co.leaf2u_api.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {

    /**
     * 적금계좌 idx로 납입내역 리스트 가져오기
     * @param accountIdx
     * @return List<AccountHistory>
     */
    @Query("""
        SELECT sah, FUNCTION('DATE_FORMAT', sah.paymentDate, '%m-%d') AS formattedDate
        FROM AccountHistory sah
        WHERE sah.account.idx = :accountIdx
        ORDER BY sah.paymentDate DESC
    """)
    List<AccountHistory> findAccountHistoryListByAccountIdx(@Param("accountIdx") Long accountIdx);

    /**
     * 챌린지 별 횟수
     * @param accountIdx
     * @return
     */
    @Query("""
        SELECT 
            COUNT(CASE WHEN sah.challengeType = 'T' THEN 1 END) AS countT,
            COUNT(CASE WHEN sah.challengeType = 'C' THEN 1 END) AS countC,
            COUNT(CASE WHEN sah.challengeType = 'R' THEN 1 END) AS countR
        FROM AccountHistory sah
        WHERE sah.account.idx = :accountIdx
    """)
    Map<String, Object> getChallengeCnt(@Param("accountIdx") Long accountIdx);

    /**
     * 납입일 리스트 date 포맷
     * @param accountIdx
     * @return
     */
    @Query("""
        SELECT DATE(sah.paymentDate) AS paymentDate
        FROM AccountHistory sah
        WHERE sah.account.idx = :accountIdx
    """)
    List<String> getFormatPaymentDate(@Param("accountIdx") Long accountIdx);
}
