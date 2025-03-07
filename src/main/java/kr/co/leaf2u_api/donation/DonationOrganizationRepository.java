package kr.co.leaf2u_api.donation;

import kr.co.leaf2u_api.entity.DonationOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationOrganizationRepository extends JpaRepository<DonationOrganization, Long> {
}
