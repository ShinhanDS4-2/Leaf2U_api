package kr.co.leaf2u_api.donation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController  // 이 클래스가 REST API요청을 처리하는 컨트롤러임을 명시
@RequestMapping("/api/donation")  // 기본 URL 경로 설정
@RequiredArgsConstructor  // lombok -> final이 붙은 필드를 자동으로 생성자 주입
public class DonationController {

    private final DonationService donationService;  // DonationService 객체를 생성자 주입 방식으로 자동 주입해줌

// 사용자 인증 매번해야함. api 호출시마다 MEMBER(사용자테이블) email 받아야함
// 1. 후원단체 리스트 페이지 관련
    /** (1)
     * 후원단체 리스트 조회
     * @param X
     * @return List<DonationOrganizationDTO>
     */
    @GetMapping("/organizationList")
    public ResponseEntity<List<DonationOrganizationDTO>> getDonationOrganizationList() {
        List<DonationOrganizationDTO> donationOrganizationList = donationService.getDonationOrganizationList();
        // donationOrganizationList라는 이름의 변수에 서비스 메서드를 호출하여 반환된 값을 저장
        return ResponseEntity.ok(donationOrganizationList);
        // ResponseEntity.ok()는 HTTP 200 OK 상태 코드를 반환하면서, 
        // donationOrganizationList 리스트를 응답 본문에 담아 클라이언트 객체로 전달한다. 
    }


    /** (2)
     * 후원단체 상세정보 조회
     * @param DONATION_ORGANIZATION(후원단체테이블) idx
     * @return idx에 값에 해당하는 DonationOrganizationDTO
     */
    @GetMapping("organizationDetail/{idx}")  // idx값을 경로 변수로 받을 수 있도록
    public ResponseEntity<DonationOrganizationDTO> getDonationOrganizationDetail(@PathVariable Long donationOrganizationIdx) {
        Optional<DonationOrganizationDTO> donationOrganizationDetail = donationService.getDonationOrganizationDetail(donationOrganizationIdx);
        return donationOrganizationDetail.map(ResponseEntity::ok)  // 값이 있으면 DonationOrganizationDTO 객체를 200 OK 응답으로 반환
                // ResponseEntity::ok는 DonationOrganizationDTO 객체를 ResponseEntity로 래핑하여 HTTP 응답으로 반환하는 함수
                .orElseGet(() -> ResponseEntity.notFound().build());  // .orElseGet() => 객체에 값이 없을 때
                            // ㄴ 값이 없으면 ResponseEntity.notFound().build()를 실행하여 404 응답을 반환
    }

// 2. 후원내역 페이지 관련
    /** (1)
     * 후원내역 리스트 조회
     * @param MEMBER(사용자테이블) idx
     * @return List, Count
     */
    @GetMapping("historyList/{memberIdx}")  // 사용자 idx값을 경로 변수로 받을 수 있도록 하는게 맞나..?
    public ResponseEntity<Map<String, Object>> getDonationHistoryList(@PathVariable Long memberIdx) {
        Map<String, Object> donationHistoryList = donationService.getDonationHistoryList(memberIdx);
        return ResponseEntity.ok(donationHistoryList);  // 응답 반환 (상태 코드 200과 함께 Map 반환)
    }


    /** (2)
     * 후원내역 상세정보 조회
     * @param DONATION_HISTORY(후원내역) idx
     * @return donationHistoryDetail(기부내역), donationOrganization(기부처)
     */
    @GetMapping("historyDetail/{donationHistoryIdx}")  // 사용자 idx값을 경로 변수로 받을 수 있도록 하는게 맞나..?
    public ResponseEntity<Map<String, Object>> getDonationHistoryDetail(@PathVariable Long donationHistoryIdx) {
        Map<String, Object> donationHistoryDetail = donationService.getDonationHistoryDetail(donationHistoryIdx);
        return ResponseEntity.ok(donationHistoryDetail);
    }

    /** (3)
     * 후원증서는 어디로 들어가야하지 ??
     * @param
     * @return
     */


}