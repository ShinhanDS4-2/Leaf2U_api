package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@ToString(exclude = {"member", "account", "organization"})
@AllArgsConstructor
@NoArgsConstructor
public class DonationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable=false, precision = 8, scale = 0)
    private BigDecimal donationAmount;

    @Column(nullable=false, precision = 3, scale = 0)
    private BigDecimal ratio;

    @Column(nullable=false)
    private LocalDateTime donationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_account_idx", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_idx", nullable = false)
    private DonationOrganization organization;
}
