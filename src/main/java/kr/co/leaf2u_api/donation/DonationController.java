package kr.co.leaf2u_api.donation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController  // 이 클래스가 REST API요청을 처리하는 컨트롤러임을 명시
@RequestMapping("/api/donation")  // 기본 URL 경로 설정
@RequiredArgsConstructor  // lombok -> final이 붙은 필드를 자동으로 생성자 주입
public class DonationController {

    private final DonationService donationService;  // DonationService 객체를 생성자 주입 방식으로 자동 주입해줌

    /* 후원 관리 API - 시온 */
    /** (1) 후원단체 리스트 조회
     * @param
     * @return List<DonationOrganizationDTO>
     */
    @GetMapping("/organizationList")
    public ResponseEntity<List<DonationOrganizationDTO>> getDonationOrganizationList() {
        List<DonationOrganizationDTO> donationOrganizationList = donationService.getDonationOrganizationList();
        return ResponseEntity.ok(donationOrganizationList);
    }

    /** (2)후원단체 상세정보 조회  ==>  리액트 뿌려놓고 보니 필요없을듯 ??
     * @param donationOrganizationIdx
     * @return DonationOrganizationDTO
     */
    @GetMapping("organizationDetail/{donationOrganizationIdx}")
    public ResponseEntity<DonationOrganizationDTO> getDonationOrganizationDetail(@PathVariable Long donationOrganizationIdx) {
        DonationOrganizationDTO donationOrganizationDetail = donationService.getDonationOrganizationDetail(donationOrganizationIdx);
        return ResponseEntity.ok(donationOrganizationDetail);
    }

    /** (1) 후원내역 리스트 조회
     * @return List, Count
     */
    @GetMapping("historyList")
    public ResponseEntity<Map<String, Object>> getDonationHistoryList() {
        Map<String, Object> donationHistoryList = donationService.getDonationHistoryList();
        return ResponseEntity.ok(donationHistoryList);  // 응답 반환 (상태 코드 200과 함께 Map 반환)ㅇ
    }

    /** (2) 후원내역 상세정보 조회
     * @param donationHistoryIdx
     * @return donationHistory(기부내역), donationOrganization(기부처)
     */
    @GetMapping("historyDetail/{donationHistoryIdx}")
    public ResponseEntity<Map<String, Object>> getDonationHistoryDetail(@PathVariable Long donationHistoryIdx) {
        Map<String, Object> donationHistoryDetail = donationService.getDonationHistoryDetail(donationHistoryIdx);
        return ResponseEntity.ok(donationHistoryDetail);
    }

    /* 후원 기여도 api - 문경미 */
    /**
     * 후원 기여도
     * @return
     */
    @PostMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDonationStatistics() {

        Map<String, Object> result = donationService.getDonationStatistics();

        return ResponseEntity.ok(result);
    }
}