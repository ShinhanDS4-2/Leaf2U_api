package kr.co.leaf2u_api.donation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 후원내역 리스트/상세 DTO
@Getter
@Setter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동으로 생성
@NoArgsConstructor  // 기본 생성자
public class DonationHistoryDTO {

    private Long idx;  // 기부내역 ID
    private String organizationName;  // 기부처 이름
    private String accountNumber;  // 계좌번호
    private BigDecimal donationAmount;  // 기부금액
    private LocalDateTime donationDate;  // 기부일

    // 추가적인 필드들
    private BigDecimal interest;  // 이자기부금
    private BigDecimal principal;  // 원금기부금
    private BigDecimal point;  // 포인트기부금
    private BigDecimal interestRate;  // 기본 금리
    private BigDecimal primeRate;  // 우대 금리
    private BigDecimal finalInterestRate;  // 최종 금리

    // (1) 후원내역 리스트 반환하는 쿼리에서 사용하는 생성자
    public DonationHistoryDTO(Long donationHistoryIdx, String organizationName, String accountNumber, BigDecimal donationAmount, LocalDateTime donationDate) {
        this.idx = donationHistoryIdx;
        this.organizationName = organizationName;
        this.accountNumber = accountNumber;
        this.donationAmount = donationAmount;
        this.donationDate = donationDate;
    }

    // (3) 후원내역 상세정보(기부내역) 반환하는 쿼리에서 사용하는 생성자
    public DonationHistoryDTO(Long donationHistoryIdx, BigDecimal donationAmount, BigDecimal interest, BigDecimal principal,
                              BigDecimal point, LocalDateTime donationDate, String accountNumber, BigDecimal interestRate,
                              BigDecimal primeRate, BigDecimal finalInterestRate) {
        this.idx = donationHistoryIdx;
        this.donationAmount = donationAmount;
        this.interest = interest;
        this.principal = principal;
        this.point = point;
        this.donationDate = donationDate;
        this.accountNumber = accountNumber;
        this.interestRate = interestRate;
        this.primeRate = primeRate;
        this.finalInterestRate = finalInterestRate;
    }


}
