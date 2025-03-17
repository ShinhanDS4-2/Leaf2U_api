package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.AccountHistory;
import kr.co.leaf2u_api.entity.InterestRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    //사용한 모든 적금 조회
    List<Account>findByMemberIdx(Long memberIdx);

    //현재 사용 중인 적금 조회, N이 현재 사용 중
    @Query("SELECT a FROM Account a WHERE a.member.idx=:memberIdx AND a.accountStatus='N'")
    Optional<Account> findAccountByMember(Long memberIdx);

/** 적금 계좌 관리 - 시온 */
    /** 계좌 기본 정보 (현재 계좌상태가 '정상N'인 것만)
     * @param memberIdx
     */
    @Query("SELECT a FROM Account a WHERE a.member.idx=:memberIdx AND a.accountStatus='N'")
    Optional<Account> getAccountInfoByIdx(@Param("memberIdx") Long memberIdx);

    // 계좌idx를 기준으로 Account엔티티 조회
    Optional<Account> findByIdx(Long idx);

    /** 금리내역 조회
     * @param accountIdx
     */
    @Query("SELECT i FROM InterestRateHistory i WHERE i.account.idx=:accountIdx")
    List<InterestRateHistory> getInterestRateHistory(@Param("accountIdx") Long accountIdx);

    /** 납입내역 조회
     * @param accountIdx
     */
    @Query("SELECT ah FROM AccountHistory ah WHERE ah.account.idx=:accountIdx")
    List<AccountHistory> getAccountHistory(@Param("accountIdx") Long accountIdx);

    /** 금리타입rate_type 별 금리 합계 조회
     * @param accountIdx
     */
    @Query(value = "SELECT " +
            "SUM(CASE WHEN irh.rate_type = 'B' THEN irh.rate ELSE 0 END) as B, " +
            "SUM(CASE WHEN irh.rate_type = 'C' THEN irh.rate ELSE 0 END) as C, " +
            "SUM(CASE WHEN irh.rate_type = 'E' THEN irh.rate ELSE 0 END) as E, " +
            "SUM(CASE WHEN irh.rate_type = 'F' THEN irh.rate ELSE 0 END) as F, " +
            "SUM(CASE WHEN irh.rate_type = 'D' THEN irh.rate ELSE 0 END) as D, " +
            "SUM(CASE WHEN irh.rate_type = 'W' THEN irh.rate ELSE 0 END) as W " +
            "FROM interest_rate_history irh " +
            "JOIN saving_account sa ON irh.saving_account_idx = sa.idx " +
            "WHERE sa.idx = :accountIdx", nativeQuery = true)
    Object[] rateSumByType(@Param("accountIdx") Long accountIdx);


    /* 만기 해지  - 문경미 */
    @Modifying
    @Query("""
        UPDATE Account sa
        SET sa.interestAmount = :afterTaxInterest,
            sa.accountStatus = 'M',
            sa.endDate = now(),
            sa.updateDate = now()
        WHERE sa.idx = :accountIdx
    """)
    void updateMaturity(@Param("accountIdx") Long accountIdx, @Param("afterTaxInterest")BigDecimal interestAmount);

    // 납입 계좌 정보
    @Query("""
    SELECT m.name,
           sa.paymentAmount,
           SUBSTRING(sa.accountNumber, LENGTH(sa.accountNumber) - 3, 4),
           SUBSTRING(c.accountNumber, LENGTH(c.accountNumber) - 3, 4)
    FROM Account sa
    JOIN sa.member m
    JOIN AccountCard ac ON ac.account.idx = sa.idx
    JOIN ac.card c
    WHERE sa.idx = :accountIdx
    AND sa.accountStatus = 'N'
""")
    List<Object[]> findAccountInfo(@Param("accountIdx") Long accountIdx);



}