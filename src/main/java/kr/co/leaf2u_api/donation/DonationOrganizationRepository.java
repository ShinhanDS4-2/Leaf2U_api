package kr.co.leaf2u_api.donation;

import kr.co.leaf2u_api.entity.DonationOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// JPA의 JpaRepository룰 상속하여 DonationHistory 데이터를 관리하는 저장소(Repository) 역할
@Repository
public interface DonationOrganizationRepository extends JpaRepository<DonationOrganization, Long> {
                                            // ㄴ <관리할 엔티티 클래스(DB테이블과 매핑), 엔티티의 PK 타입>
}
