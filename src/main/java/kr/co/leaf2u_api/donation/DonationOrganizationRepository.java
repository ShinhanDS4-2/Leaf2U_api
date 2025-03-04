package kr.co.leaf2u_api.donation;

import kr.co.leaf2u_api.entity.DonationOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// JPA의 JpaRepository룰 상속하여 DonationHistory 데이터를 관리하는 저장소(Repository) 역할
@Repository
public interface DonationOrganizationRepository extends JpaRepository<DonationOrganization, Long> {
                                            // ㄴ <관리할 엔티티 클래스(DB테이블과 매핑), 엔티티의 PK 타입>
/**
 *  JPA에서 기본적으로 제공하는 메서드는 엔티티 객체를 반환 => DTO 변환이 필요함
 *  findById(), findAll(), save(), delete()와 같은 메서드는 모두 엔티티를 대상으로 작업을 수행하고, 엔티티 객체를 반환함.
 * */
}
