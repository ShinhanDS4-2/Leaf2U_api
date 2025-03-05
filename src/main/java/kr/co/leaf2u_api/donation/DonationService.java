package kr.co.leaf2u_api.donation;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface DonationService {

    // 후원단체 리스트 조회 (DTO 객체 반환)
    List<DonationOrganizationDTO> getDonationOrganizationList();

    // 후원단체 상세 조회 (DTO 객체 반환)
    DonationOrganizationDTO getDonationOrganizationDetail(Long donationOrganizationIdx);

    // 후원내역 리스트 조회 (DTO 객체 반환)
    Map<String, Object> getDonationHistoryList(Long memberIdx);

    // 후원내역 상세정보 조회 (DTO 객체 반환)
    Map<String, Object> getDonationHistoryDetail(Long donationHistoryIdx);

    // 후원 기여도
    Map<String, Object> getDonationStatistics(Map<String, Object> param);
}
