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

    @Column
    private char earnType;

    @Column(precision = 10, scale = 2) // 정수 8자리 + 소수점 2자리
    private BigDecimal earnPoint;

    @Column(precision = 10, scale = 2)
    private BigDecimal usePoint;


    @Column
    private LocalDateTime earnDate;

    @Column
    private LocalDateTime useDate;

    @ManyToOne(fetch = FetchType.EAGER) // 즉시 로드로 변경
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

}
