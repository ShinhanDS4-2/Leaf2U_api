package kr.co.leaf2u_api.feedback;

import kr.co.leaf2u_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Account, Long> {

    /**
     * 1. 특정 사용자의 챌린지 수행 횟수
     * - 사용자의 saving_cnt 값을 가져온다.
     */
    @Query(value = """
        SELECT sa.saving_cnt 
        FROM saving_account sa 
        WHERE sa.idx = :accountIdx
    """, nativeQuery = true)
    int getUserChallengeCount(@Param("accountIdx") Long accountIdx);

    /**
     * 2. 전체 평균 챌린지 수행 횟수
     * - 모든 사용자의 saving_cnt 값을 평균 내고, 소수점 첫째 자리에서 반올림한다.
     */
    @Query(value = """
        SELECT IFNULL(SUM(sa.saving_cnt) / COUNT(sa.saving_cnt), 0)
        FROM saving_account sa
    """, nativeQuery = true)
    int getAverageChallengeCount();
}
