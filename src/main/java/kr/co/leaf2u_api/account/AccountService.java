package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AccountService {

    Account createAccount(AccountDTO accountDTO);                                  //적금 생성
    List<Account> getAccountsByMember(Long memberId);                              //적금 조회

 /* 적금 계좌 관리 - 시온 */
    Account getAccountInfoById(Long memberIdx);  // (1) 기본 정보 조회
    int updatePaymentAmount(AccountDTO accountDTO);  // (2) 납입금액 변경
    Map<String, Object> getMaturityInterest(AccountDTO accountDTO);  // (3) 예상 이자 조회 - 만기일 해지
    // (3) 예상 이자 조회 - 오늘 해지
    // (3) 예상 이자 조회 - 선택일자 해지

    int terminateAccount(AccountDTO accountDTO);  // (4) 계좌 해지

}
