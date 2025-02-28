package kr.co.leaf2u_api.entity;

import jakarta.persistence.*;
import kr.co.leaf2u_api.donation.DonationOrganizationDTO;
import lombok.*;

@Entity  // DB테이블과 매핑되는 엔티티 클래스
@Table(name="DONATION_ORGANIZATION")  // 실제 DB 테이블명 지정
@Getter
@Setter
@ToString
@NoArgsConstructor  // JPA에서 기본 생성자 필요
// @Builder  ->  엔티티 클래스에서는 @Builder를 전체 필드에 적용하지 말고, 생성자에 적용하는 것이 좋음
// @AllArgsConstructor  ->  @Builder쓰면 얘는 안써도됌
public class DonationOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본키 자동 증가 설정
    private Long idx;

    @Column(nullable = false)
    private String name;  // 단체명

    @Column(nullable = false)
    private String telNumber;  // 연락처

    @Column(nullable = false)
    private String description;  // 단체 설명

    // 특정 생성자에만 @Builder 적용
    @Builder
    public DonationOrganization(String name, String telNumber, String description) {
        this.name = name;
        this.telNumber = telNumber;
        this.description = description;
    }

    // DTO -> Entity 변환 메서드(엔티티 클래스에 작성해야함)
    public static DonationOrganization dtoToEntity(DonationOrganizationDTO dto) {
        return DonationOrganization.builder()
                .name(dto.getName())
                .telNumber(dto.getTelNumber())
                .description(dto.getDescription())
                .build();  // 빌더 패턴을 통해 객체 생성
    }
}
