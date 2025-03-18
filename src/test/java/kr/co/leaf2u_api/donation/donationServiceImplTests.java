package kr.co.leaf2u_api.donation;
import kr.co.leaf2u_api.entity.DonationOrganization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class donationServiceImplTests {

    @Autowired
    private DonationOrganizationRepository donationOrganizationRepository;

    @Test
    public void insertDonationOrganization() {
        DonationOrganization donationOrganization= DonationOrganization.builder()
                .description("설명설명 존재하지 않습니다.")
                .name("이름이름")
                .telNumber("222-3456-8766")
                .build();
        donationOrganizationRepository.save(donationOrganization);
    }
}
