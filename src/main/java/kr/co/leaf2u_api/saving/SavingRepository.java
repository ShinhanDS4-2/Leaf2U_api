package kr.co.leaf2u_api.saving;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SavingRepository extends JpaRepository<AccountHistory, Long> {

    /**
     * 🔹 1️⃣ 카드 잔액 차감 (Native Query로 변경)
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE card c
        JOIN account_card ac ON c.idx = ac.card_idx
        JOIN saving_account sa ON ac.saving_account_idx = sa.idx
        SET c.balance = c.balance - sa.payment_amount,
            c.update_date = NOW()
        WHERE c.idx = (
            SELECT ac.card_idx FROM account_card ac WHERE ac.saving_account_idx = :accountIdx
        )
        AND c.balance >= sa.payment_amount
    """, nativeQuery = true)
    void updateCardBalance(@Param("accountIdx") Long accountIdx);


    /**
     * 🔹 2️⃣ 적금 납입 내역 추가
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO saving_account_history (saving_account_idx, member_idx, payment_amount, cumulative_amount, payment_date, challenge_type)
        SELECT\s
            sa.idx,          \s
            sa.member_idx,   \s
            sa.payment_amount, \s
            IFNULL((SELECT SUM(payment_amount) FROM saving_account_history WHERE saving_account_idx = sa.idx), 0) + sa.payment_amount AS cumulative_amount, \s
            NOW(),
            :challengeType
        FROM saving_account sa
        WHERE sa.member_idx = :memberIdx
    """, nativeQuery = true)
    void insertSavingHistory(@Param("memberIdx") Long memberIdx, @Param("challengeType") String challengeType);


    /**
     * 🔹 3️⃣ 매일 금리 (D) 추가
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO interest_rate_history (saving_account_idx, saving_account_history_idx, rate_type, rate, create_date)
        SELECT\s
         sah.saving_account_idx, \s
         sah.idx AS saving_account_history_idx, \s
         'D' AS rate_type,
         0.1 AS rate,
         NOW()
        FROM saving_account_history sah
        WHERE sah.idx = (
         SELECT MAX(idx)\s
         FROM saving_account_history\s
         WHERE DATE(payment_date) = CURDATE()
        )
    """, nativeQuery = true)
    void insertDailyInterest(@Param("accountIdx") Long accountIdx);


    /**
     * 🔹 4️⃣ 7번째 납입 시 연속 금리 (W) 추가
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO interest_rate_history (saving_account_idx, saving_account_history_idx, rate_type, rate, create_date)
        SELECT\s
            sah.saving_account_idx, \s
            sah.idx AS saving_account_history_idx, \s
            'W' AS rate_type,
            0.2 AS rate,
            NOW()
        FROM saving_account_history sah
        JOIN (
            -- 'D' 금리가 7번째 추가된 경우만 찾음
            SELECT saving_account_idx, COUNT(*) AS d_count
            FROM interest_rate_history
            WHERE rate_type = 'D'
            GROUP BY saving_account_idx
            HAVING d_count % 7 = 0
        ) seq ON sah.saving_account_idx = seq.saving_account_idx
        WHERE sah.idx = (
            -- 가장 최근 추가된 saving_account_history의 idx만 선택
            SELECT MAX(idx)\s
            FROM saving_account_history\s
            WHERE DATE(payment_date) = CURDATE()
        )
    """, nativeQuery = true)
    void insertWeeklyInterest(@Param("accountIdx") Long accountIdx);


    /**
     * 🔹 5️⃣ prime_rate 업데이트
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE saving_account 
        SET prime_rate = (
            SELECT SUM(irh.rate)
            FROM interest_rate_history irh
            WHERE irh.saving_account_idx = :accountIdx
            AND irh.rate_type NOT IN ('B')
        )
        WHERE idx = :accountIdx
    """, nativeQuery = true)
    void updatePrimeRate(@Param("accountIdx") Long accountIdx);


    /**
     * 🔹 6️⃣ 최종 금리 업데이트
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE saving_account 
        SET final_interest_rate = interest_rate + prime_rate 
        WHERE idx = :accountIdx
    """, nativeQuery = true)
    void updateFinalInterestRate(@Param("accountIdx") Long accountIdx);


    /**
     * 🔹 7️⃣ 적금 계좌 잔액(balance) 업데이트
     */
    @Modifying
    @Transactional
    @Query(value = """
    UPDATE saving_account sa
    SET sa.balance = (
        SELECT sah.cumulative_amount
        FROM saving_account_history sah
        WHERE sah.saving_account_idx = sa.idx
        ORDER BY sah.payment_date DESC
        LIMIT 1
    )
    WHERE sa.idx = :accountIdx
""", nativeQuery = true)
    void updateSavingAccountBalance(@Param("accountIdx") Long accountIdx);

    /**
     * 🔹 8️⃣ 적금 납입 횟수(saving_cnt) 업데이트
     */
    @Modifying
    @Transactional
    @Query(value = """
    UPDATE saving_account sa
    SET sa.saving_cnt = (
        SELECT COUNT(*)
        FROM interest_rate_history irh
        WHERE irh.saving_account_idx = sa.idx
        AND irh.rate_type = 'D'
    )
    WHERE sa.idx = :accountIdx
""", nativeQuery = true)
    void updateSavingCount(@Param("accountIdx") Long accountIdx);

}
