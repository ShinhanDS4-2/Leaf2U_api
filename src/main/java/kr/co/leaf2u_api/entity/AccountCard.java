package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_card")
public class AccountCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_account_idx", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "card_idx",nullable = false)
    private Card card;

}
