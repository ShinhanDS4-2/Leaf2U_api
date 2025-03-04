package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import kr.co.leaf2u_api.donation.DonationHistoryDTO;
import kr.co.leaf2u_api.donation.DonationOrganizationDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="DONATION_HISTORY")  // 실제 DB 테이블명 지정
@Builder
@Getter
@Setter
@ToString(exclude = {"member", "account", "organization"})  // exclude 속성 => 해당필드는 toString메서드에서 제외
                           // ㄴ 모두 다른 엔티티와 관계가 있기 때문에 순환 참조가 발생할 수 있어서 제외함
@AllArgsConstructor
@NoArgsConstructor
public class DonationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_account_idx", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_idx", nullable = false)
    private DonationOrganization donationOrganization;
    // ㄴ DonationHistory엔티티의 organizationIdx 필드는 DonationOrganization 엔티티를 참조하고 있음을 명시해줌
    // ㄴ 조인할 때 JOIN DonationHistory.organizationIdx 라고 하면 DonationOrganization 엔티티와 자동 조인됨(테이블 명시X)
    /**
     * SQL은 쿼리문에서 테이블명을 수동으로 지정해서 조인해주는거고
     * JPQL은 엔티티 클래스에서 미리 자동으로 조인이 되어있는거라고 이해하면됨
     * */

    @Column(nullable = false, precision = 8)
    private BigDecimal donationAmount;

    @Column(nullable = false, precision = 3)
    private BigDecimal interest;

    @Column(nullable = false, precision = 7)
    private BigDecimal principal;

    @Column(nullable = false, precision = 4)
    private BigDecimal point;

    @Column(nullable = false)
    private LocalDateTime donationDate;


    // DTO -> Entity 변환 메서드(엔티티 클래스에 작성해야함)
    public DonationHistory dtoToEntity(DonationHistoryDTO dto) {
        return DonationHistory.builder()
                .donationAmount(dto.getDonationAmount())
                .interest(dto.getInterest())
                .principal(dto.getPrincipal())
                .point(dto.getPoint())
                .donationDate(dto.getDonationDate())
                .build();  // 빌더 패턴을 통해 객체 생성
    }
}
