package kr.co.leaf2u_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private char account_status;

    @Column(nullable = false)
    private String account_number;

    @Column(nullable = false)
    private String account_password;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private BigDecimal interest_rate;

    @Column(nullable = false)
    private BigDecimal prime_rate;

    @Column(nullable = false)
    private char taxation_yn;

    private LocalDateTime maturity_date;

    private BigDecimal interest_amount;

}
