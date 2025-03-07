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
public class AccountCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name="card_idx")
    private Card card;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name="saving_account_idx")
    private Account account;
}
