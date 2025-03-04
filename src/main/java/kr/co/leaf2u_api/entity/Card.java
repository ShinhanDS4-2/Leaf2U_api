package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Card extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name="member_idx")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false,name = "saving_account_idx")
    private Account account;

    @Column(nullable = false)
    private char cardType;

    @Column(nullable = false)
    private String cardName;

    @Column(nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false, precision = 10)
    private BigDecimal balance;

    @Column(nullable = false)
    private String cardPassword;

}
