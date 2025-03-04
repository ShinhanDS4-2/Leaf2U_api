package kr.co.leaf2u_api.saving;

import kr.co.leaf2u_api.entity.InterestRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestRateHistoryRepository extends JpaRepository<InterestRateHistory, Long> {

    /**
     * 납입내역 별 누적 금리 리스트 가져오기
     * @param accountHistoryIdx
     * @return List<InterestRateHistory>
     */
    @Query("""
        SELECT irh
        FROM InterestRateHistory irh
        WHERE irh.accountHistory.idx = :accountHistoryIdx
        ORDER BY irh.createDate DESC
    """)
    List<InterestRateHistory> findInterestRateHistoryListByAccountHistoryIdx(@Param("accountHistoryIdx") Long accountHistoryIdx);

}
