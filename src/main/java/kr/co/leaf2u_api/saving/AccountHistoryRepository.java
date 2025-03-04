package kr.co.leaf2u_api.saving;

import kr.co.leaf2u_api.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}
