package kr.co.leaf2u_api.donation;

import kr.co.leaf2u_api.entity.DonationOrganization;
import kr.co.leaf2u_api.saving.AccountHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// DB 변경(삽입, 수정, 삭제)이 필요하면 @Transactional을 추가해야 함
public class DonationServiceImpl implements DonationService {

    private final DonationOrganizationRepository donationOrganizationRepository;  // 후원단체 레파지토리 주입
    private final DonationHistoryRepository donationHistoryRepository;  // 후원내역 레파지토리 주입
    private final AccountHistoryRepository accountHistoryRepository; // 납입 내역 레파지토리

    /* 각 항목 별 탄소발자국 */
    private final double TUMBLER_CARBON = 45.84;
    private final double RECEIPT_CARBON = 3;
    private final double BICYCLE_CARBON = 2278;

// 1. 후원단체 리스트 페이지 관련
    /** (1)후원단체 리스트 조회 (완료)
     * @return List<DonationOrganizationDTO>
     */
    @Override
    public List<DonationOrganizationDTO> getDonationOrganizationList() {
        List<DonationOrganization> donationOrganizationList = donationOrganizationRepository.findAll();  // 레파지토리에서 후원 단체 목록 전체 가져옴 (실제 엔티티값)

        System.out.println(donationOrganizationList); // 출력하여 데이터 확인

        // 엔티티를 DTO로 변환 후 반환
        return donationOrganizationList.stream()  // Stream을 이용하여 변환
                .map(donationOrganization -> DonationOrganizationDTO.builder()  // builder 사용
                        .organizationIdx(donationOrganization.getIdx())  // 필드값 설정
                        .name(donationOrganization.getName())
                        .telNumber(donationOrganization.getTelNumber())
                        .description(donationOrganization.getDescription())
                        .build())  // 빌더로 객체 생성
                .collect(Collectors.toList());  // 변환된 DTO 목록을 리스트로 모아 반환
    }

    /** (2)후원단체 상세정보 조회 (완료)
     * @param donationOrganizationIdx
     * @return Optional<DonationOrganizationDTO>
     */
    @Override
    public DonationOrganizationDTO getDonationOrganizationDetail(Long donationOrganizationIdx) {
        Optional<DonationOrganization> donationOrganizationDetail = donationOrganizationRepository.findById(donationOrganizationIdx);

        // 엔티티를 DTO로 변환 후 반환
        return donationOrganizationDetail.map(donationOrganization -> DonationOrganizationDTO.builder()
                .organizationIdx(donationOrganization.getIdx())
                .name(donationOrganization.getName())
                .telNumber(donationOrganization.getTelNumber())
                .description(donationOrganization.getDescription())
                .build())  // 빌더로 객체 생성
                .orElse(null);  // .map -> Optional에서 값 꺼내서 반환 (값 있으면 그대로 반환, 값 없으면 null반환)
    }  // => 리스트는 Optional을 반환할 필요가 없음


// 2. 후원내역 페이지 관련
    /** (1) 후원내역 리스트 조회 (완료)
     * @param memberIdx
     * @return List, Count
     */
    @Override
    public Map<String, Object> getDonationHistoryList(Long memberIdx) {
        // 후원내역 리스트
        List<DonationHistoryDTO> donationHistoryList = donationHistoryRepository.getDonationHistoryList(memberIdx);
        // 후원내역 리스트 개수
        long count = donationHistoryRepository.countByDonationHistoryList(memberIdx);

        // 결과를 Map으로 묶어서 반환
        Map<String, Object> result = new HashMap<>();
        result.put("List", donationHistoryList);
        result.put("Count", count);

        return result;
    }


    /** (2) 후원내역 상세정보 조회 (완료)
     * @param
     * @return donationHistory(기부내역), donationOrganization(기부처)
     */
    @Override
    public Map<String, Object> getDonationHistoryDetail(Long donationHistoryIdx) {
        // 기부처 조회
        Optional<DonationOrganizationDTO> donationOrganization = donationHistoryRepository.getDonationOrganization(donationHistoryIdx);
        // 기부내역 조회
        Optional<DonationHistoryDTO> donationHistoryDetail = donationHistoryRepository.getDonationHistoryDetail(donationHistoryIdx);

        // 결과를 담을 Map 생성
        Map<String, Object> result = new HashMap<>();

        // 두 객체가 모두 존재할 경우 Map에 담아서 반환
        if (donationHistoryDetail.isPresent() && donationOrganization.isPresent()) {
            result.put("donationOrganization", donationOrganization.orElse(null));
            result.put("donationHistory", donationHistoryDetail.orElse(null));  // Optional 객체에서 값꺼내 반환 -> 값이 없으면 null반환
        }
        // 결과가 없을 경우 빈 Map 반환
        return result;
    }

    /** (3)
     * 후원증서는 어디로 들어가야하지 ??
     * @param
     * @return
     */


    /* 후원 기여도 api */

    /**
     * 후원 기여도
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getDonationStatistics(Map<String, Object> param) {

        Map<String, Object> result = new HashMap<>();

        // 랭킹
        List<Map<String, Object>> ranking = donationHistoryRepository.findDonationRanking();
        result.put("ranking", ranking);

        // 후원 퍼센테이지
        Long memberIdx = Long.parseLong(String.valueOf(param.get("memberIdx")));
        Map<String, Object> map = donationHistoryRepository.findDonationSums(memberIdx);
        result.putAll(map);

        int myTotal = ((BigDecimal) map.get("my_total")).intValue();
        int ageTotal = ((BigDecimal) map.get("age_total")).intValue();
        int allTotal = ((BigDecimal) map.get("all_total")).intValue();

        int myRatio = (int) ((myTotal / (double) allTotal) * 100);
        int ageRatio = (int) ((ageTotal / (double) allTotal) * 100);

        result.put("my_ratio", myRatio);
        result.put("age_ratio", ageRatio);

        // 탄소발자국 계산
        Long accountIdx = Long.parseLong(String.valueOf(param.get("accountIdx")));
        Map<String, Object> challengeCnt = accountHistoryRepository.getChallengeCnt(accountIdx);

        long tumbler = (long) challengeCnt.get("countT");
        long receipt = (long) challengeCnt.get("countR");
        long bicycle = (long) challengeCnt.get("countC");

        double carbon = (tumbler * TUMBLER_CARBON) + (receipt * RECEIPT_CARBON) + (bicycle * BICYCLE_CARBON);
        carbon = Math.round(carbon * 100) / 100.0;
        result.put("carbon", carbon);

        return result;
    }
}
