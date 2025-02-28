package kr.co.leaf2u_api.donation;

import kr.co.leaf2u_api.entity.DonationHistory;
import kr.co.leaf2u_api.entity.DonationOrganization;
import kr.co.leaf2u_api.donation.DonationOrganizationDTO;  // donation 패키지에서 임포트

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// 서비스 구현 파일에서는 레파지토리에서 제공하는 메서드를 호출해서 DB에서 실제 데이터를 가져옴

@Service  // 이 클래스가 서비스 역할을 한다는 것을 Spring에 알려줌 => 비즈니스 로직을 처리하는 계층
@RequiredArgsConstructor  // final이 붙은 필드를 자동으로 주입하는 생성자를 생성 (의존성 주입을 편리하게 처리하기 위함)
// DB 변경(삽입, 수정, 삭제)이 필요하면 @Transactional을 추가해야 함
public class DonationServiceImpl implements DonationService {

    private final DonationOrganizationRepository donationOrganizationRepository;  // 후원단체 레파지토리 주입
    private final DonationHistoryRepository donationHistoryRepository;  // 후원내역 레파지토리 주입

    // 후원단체 Entity -> DTO 변환 메서드
    private DonationOrganizationDTO entityToDTO(DonationOrganization entity) {
        return new DonationOrganizationDTO(
                entity.getIdx(),
                entity.getName(),
                entity.getTelNumber(),
                entity.getDescription()
        );
    }
    
    // 후원내역 Entity -> DTO 변환 메서드

// 1. 후원단체 리스트 페이지 관련
    /** (1)후원단체 리스트 조회
     * @param X
     * @return List<DonationOrganizationDTO>
     */
    @Override
    public List<DonationOrganizationDTO> getDonationOrganizationList() {
        // 레파지토리에서 후원 단체 목록 전체 가져옴 (실제 엔티티값)
        List<DonationOrganization> donationOrganizationList = donationOrganizationRepository.findAll();
        // findAll()은 엔티티 객체를 반환하기 때문에 DTO 변환이 필요함

        // DTO로 변환 후 반환
        return donationOrganizationList.stream()  // Stream을 이용하여 변환
                .map(this::entityToDTO)  // 각 엔티티를 DTO로 변환
                .collect(Collectors.toList());  // 변환된 DTO 목록을 리스트로 모아 반환

        /**
         Q. 왜 DTO로 변환 후 반환해야하는지?
         A. .findAll() 메서드가 엔티티 객체를 반환하기 때문에 DTO로 변환하는 과정이 필요함
         * */
    }

    /** (2)후원단체 상세정보 조회
     * @param DONATION_ORGANIZATION(후원단체테이블) idx
     * @return idx에 값에 해당하는 DonationOrganizationDTO
     */
    @Override
    public Optional<DonationOrganizationDTO> getDonationOrganizationDetail(Long donationOrganizationIdx) {
        // DB에서 donationOrganizationIdx에 해당하는 후원 단체를 조회
        Optional<DonationOrganization> donationOrganizationOptional = donationOrganizationRepository.findById(donationOrganizationIdx);
        // findById()로 엔티티를 조회하기 때문에 DTO 변환 필요함

        // 값이 존재하면 DTO로 변환하여 반환
        return donationOrganizationOptional.map(this::entityToDTO);
                        // Optional 사용할 때 map()을 사용하면 값이 있을때만 변환을 진행하고, 값이 없으면 변환을 하지않음
    }


// 2. 후원내역 페이지 관련
    /** (1) 후원내역 리스트 조회
     * @param MEMBER(사용자) idx
     * @return List, Count
     */
    @Override
    public Map<String, Object> getDonationHistoryList(Long memberIdx) {
        // 후원내역 리스트
        List<DonationHistoryDTO> donationHistoryList = donationHistoryRepository.getDonationHistoryList(memberIdx);
        // 후원내역 리스트 개수
        long count = donationHistoryRepository.countByDonationHistoryList(memberIdx);

        // 결과를 Map으로 묶어서 반환
        Map<String, Object> response = new HashMap<>();
        response.put("List", donationHistoryList);
        response.put("Count", count);

        return response;

        /**
         Q. 이 메서드에서는 DTO 변환없이 값을 반환하고 있음. 그게 가능한 이유는 ?
         A. JPQL쿼리에서 직접 DTO로 반환을 처리하고 있기 때문
         * */
    }


    /** (2) 후원내역 상세정보 조회
     * @param DONATION_HISTORY(후원내역) idx
     * @return
    기부처 조회 => DONATION_ORGANIZATION(name, tel_number, description)
    기부내역 조회 => SAVING_ACCOUNT(account_number, 적용금리(interest_rate+prime_rate), 원금(balance), 이자(interest_amount)), DONATION_HISTORY(후원금액 donation_amount), INTEREST_RATE_HISTORY(금리내역) 전체추출
     */

    @Override
    public Optional<DonationHistoryDTO> getDonationHistoryDetail(Long donationHistoryIdx) {
        Optional<DonationOrganization> donationOrganization= donationOrganizationRepository.findById(donationHistoryIdx);
        return Optional.empty();
    }



    /** (3)
     * 후원증서는 어디로 들어가야하지 ??
     * @param
     * @return
     */

}
