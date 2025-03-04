package kr.co.leaf2u_api.account;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class AccountDTO {
    private Long idx;  // 계좌 Idx
    private Long memberIdx;  // 사용자 Idx
    private Character accountStatus;  // 계좌 상태
    private String accountNumber;  // 계좌 번호
    private String accountPassword;  // 계좌 비밀번호
    private BigDecimal paymentAmount;  // 납입금액
    private BigDecimal balance;  // 잔액
    private BigDecimal interestRate;  // 기본 금리
    private BigDecimal primeRate;  // 우대 금리
    private BigDecimal finalInterest_rate;  // 최종 금리
    private Character taxationYn;  // 과세 구분
    private BigDecimal interestAmount;  // 이자
    private LocalDateTime createDate;  // 가입일
    private LocalDateTime endDate;  // 종료일
    private LocalDateTime updateDate;  // 수정일
    private LocalDateTime maturityDate;  // 만기일

}
