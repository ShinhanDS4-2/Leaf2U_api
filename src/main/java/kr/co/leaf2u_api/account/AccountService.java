package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AccountService {

    AccountDTO createAccount(AccountDTO accountDTO);                                  //적금 생성
    List<AccountDTO> getAccountsByMember(Long memberId);                              //적금 조회


    /* 적금 계좌 관리 - 시온 */
    Map<String, Object> getAccountInfoById();  // (1) 계좌 기본정보 조회
    int updatePaymentAmount(AccountDTO accountDTO);  // (2) 납입금액 변경
    Map<String, Object> getMaturityInterest() throws AccountNotFoundException;  // (3-1) 예상 이자 조회 - 만기일 해지
    Map<String, Object> getTodayInterest() throws AccountNotFoundException;  // (3-2) 예상 이자 조회 - 오늘 해지 getTodayInterest
    Map<String, Object> getCustomDateInterest(LocalDateTime endDate) throws AccountNotFoundException;  // (3-3) 예상 이자 조회 - 선택일자 해지 getCustomDateInterest
    int terminateAccount(String accountPassword) throws AccountNotFoundException;  // (4) 계좌 해지

    /* 메인화면에 필요한 api - 문경미 */
    Map<String, Object> getSavingInfo();                    // 메인 홈 적금 계좌 정보

    /* 만기 해지 api - 문경미 */
    Boolean maturityProcess(Map<String, Object> param);     // 만기 해지 프로세스
}
