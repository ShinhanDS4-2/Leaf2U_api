package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.InterestRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    //사용한 모든 적금 조회
    List<Account>findByMemberIdx(Long memberIdx);

    //현재 사용 중인 적금 조회, N이 현재 사용 중
    @Query("SELECT a FROM Account a WHERE a.member.idx=:memberIdx AND a.accountStatus='N'")
    Optional<Account> findAccountByMember(Long memberIdx);


    /** 적금 계좌 관리 - 시온 */
    // (1) 기본 정보 조회 (param 사용자idx) => 현재 계좌상태가 N(정상)인 것만
    @Query("SELECT a FROM Account a WHERE a.member.idx=:memberIdx AND a.accountStatus='N'")  // 계좌 상태가 정상N인 것만 조회
    Optional<Account> getAccountInfoByIdx(@Param("memberIdx") Long memberIdx);

    Optional<Account> findByIdx(Long idx);  // 계좌idx를 기준으로 Account엔티티 조회

    // (2) 납입금액 변경 (findByIdx사용)

    // (3) 예상 이자 조회
    // 적금계좌 조회 (findByIdx사용)
    // 금리내역 조회 -> InterestRateHistory의 account.idx(saving_account_idx)를 기준으로 조회
    @Query("SELECT i FROM InterestRateHistory i WHERE i.account.idx=:savingAccountIdx")
    List<InterestRateHistory> getInterestRateHistory(@Param("savingAccountIdx") Long savingAccountIdx);

    // (4) 계좌 해지 (findByIdx사용)



}
