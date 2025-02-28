package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@ToString(exclude = "account")
@AllArgsConstructor
@NoArgsConstructor
public class InterestRateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private char rateType;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal rate;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_account_idx", nullable = false)
    private Account account;
}
