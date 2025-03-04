package kr.co.leaf2u_api.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="saving_account")
public class Account extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_idx",nullable = false)
    private Member member;

    @Column(nullable = false)
    private char accountStatus;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountPassword;

    @Column(nullable = false, precision = 5)
    private BigDecimal paymentAmount;

    @Column(nullable = false,precision = 8)
    private BigDecimal balance;

    @Column(nullable = false,precision = 4,scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false,precision = 4,scale = 2)
    private BigDecimal primeRate;

    // 최종금리 컬럼 추가함 - 시온
    @Column(nullable = false,precision = 4,scale = 2)
    private BigDecimal finalInterestRate;

    @Column(nullable = false,name = "taxation_yn")
    private char taxationYn;

    @Column(precision = 5)
    private BigDecimal interestAmount;

    @Column(nullable = false)
    private LocalDateTime createDate;  // 가입일

    @Column(nullable = false)
    private LocalDateTime endDate;  // 종료일

    @LastModifiedDate  // 엔티티가 수정될 때마다 자동으로 LocalDateTime 값 업데이트
    @Column
    private LocalDateTime updateDate;  // 수정일

    @Column(nullable = false)
    private LocalDateTime maturityDate;  // 만기일


}
