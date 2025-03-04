package kr.co.leaf2u_api.donation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 후원단체 DTO
@Getter
@Setter
@AllArgsConstructor  // 생성자 자동생성
@NoArgsConstructor  // 기본 생성자
@Builder
public class DonationOrganizationDTO {
    private Long organizationIdx;  // 후원 단체 Idx
    private String name;  // 단체명
    private String telNumber;  // 연락처
    private String description;  // 단체 설명
}

