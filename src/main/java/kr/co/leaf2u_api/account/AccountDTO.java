package kr.co.leaf2u_api.account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private BigDecimal finalInterestRate;  // 최종 금리
    private Character taxationYn;  // 과세 구분
    private BigDecimal dutyRate;  // 세금비율 => 0.154 고정값
    private LocalDateTime createDate;  // 가입일
    private LocalDateTime endDate;  // 종료일(=해지일)
    private LocalDateTime updateDate;  // 수정일
    private LocalDateTime maturityDate;  // 만기일

    // 현재 잔액, 금리 조회용 생성자
    public AccountDTO(BigDecimal balance, BigDecimal finalInterestRate) {
        this.balance = balance;
        this.finalInterestRate = finalInterestRate;
    }

    // 이자 관련 추가 컬럼
    private BigDecimal preTaxInterestAmount;  // (세전)이자
    private BigDecimal taxAmount;  // 세금액
    private BigDecimal interestAmount;  // (세후)이자

}
