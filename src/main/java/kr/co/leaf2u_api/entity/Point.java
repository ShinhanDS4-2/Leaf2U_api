package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@ToString(exclude = "member")
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable=false)
    private char earnType;

    @Column(nullable=false, precision =6, scale = 0)
    private BigDecimal earnPoint;

    @Column(nullable=false, precision =6, scale = 0)
    private BigDecimal usePoint;

    @Column(nullable=false)
    private LocalDateTime earnDate;

    @Column(nullable=false)
    private LocalDateTime useDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;
}
