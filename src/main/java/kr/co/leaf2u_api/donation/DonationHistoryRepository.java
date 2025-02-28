package kr.co.leaf2u_api.donation;

import kr.co.leaf2u_api.entity.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// JPA의 JpaRepository룰 상속하여 DonationHistory 데이터를 관리하는 저장소(Repository) 역할
@Repository
public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Long> {
                                                                 // ㄴ <관리할 엔티티 클래스(DB테이블과 매핑), 엔티티의 PK 타입>

// (1) 후원내역 리스트 반환하는 JPQL 쿼리
        // DonationHistoryDTO 객체를 생성하여 반환함 (DTO 형태로 반환하기 때문에 별도의 변환 필요없음)
    @Query("SELECT new kr.co.leaf2u_api.donation.DonationHistoryDTO(dh.idx, do.name, sa.accountNumber, dh.donationAmount, dh.donationDate) " +
            "FROM DonationHistory dh " +  // 데이터를 조회할 주 엔티티 지정
            "JOIN dh.donationOrganization do " +  // DonationOrganization 엔티티 자동조인
            "JOIN dh.account sa " +  // Account 엔티티 자동조인
            "WHERE dh.member.idx = :memberIdx") // idx: Member 엔티티의 기본 키(primary key)를 의미
    // :memberIdx는 메서드 파라미터로 전달된 memberIdx 값과 비교하여 해당 회원의 후원 내역만을 조회
    List<DonationHistoryDTO> getDonationHistoryList(@Param("memberIdx") Long memberIdx);
    // @Param("memberIdx"): 메서드 파라미터로 전달된 memberIdx 값을 쿼리에 파라미터 바인딩 할 때 사용
// 메서드 파라미터에 @Param("memberIdx")로 지정했다면, 쿼리에서는 :memberIdx로 지정해야함(이름 일치)
    // 중요 => @Param어노테이션에 파라미터 이름을 명시적으로 지정만 해주면 됌!

    /**
     * do는 엔티티의 별칭이며, DonationOrganization 테이블을 참조하는 organizationIdx 필드를 의미함
     ✅JPQL에서는 테이블 이름이 아니라 엔티티 이름을 기준으로 JOIN을 수행함
     엔티티 필드를 기준으로 조인하므로 DonationOrganization 테이블을 별도 지정하지 않아도
     자동으로 organizationIdx가 DONATION_ORGANIZATION의 기본키와 조인됨

     😃 즉, JOIN dh.organizationIdx do 라고 하면,
     organizationIdx 필드가 DonationOrganization 엔티티를 가리키므로, 자동으로 해당 테이블과 JOIN이 수행되는 것이다.
     * do가 의미하는건? DonationOrganization 엔티티를 의미하는 별칭(Alias)
     * */


// (2) 후원내역 리스트 개수 반환하는 JPQL 쿼리
    @Query("SELECT COUNT(dh) FROM DonationHistory dh WHERE dh.member.idx = :memberIdx")
    long countByDonationHistoryList(@Param("memberIdx") Long memberIdx);


// (3) donationHistoryIdx 받아서 후원내역 상세정보(기부처, 기부내역) 반환하는 JPQL 쿼리
    // 기부내역 idx를 기준으로 기부처 정보 조회 (DTO 객체 반환)
    @Query("SELECT new kr.co.leaf2u_api.donation.DonationOrganizationDTO(" +
            "do.idx, " +  // 기부처 ID
            "do.name, " + // 기부처 이름
            "do.telNumber, " + // 기부처 전화번호
            "do.description) " + // 기부처 설명
            "FROM DonationOrganization do " +
            "JOIN DonationHistory dh ON dh.donationOrganization.idx = do.idx " + // 기부내역과 기부처 조인
            "WHERE dh.idx = :donationHistoryIdx")  // 기부내역 idx 기준으로 조회
    Optional<DonationOrganizationDTO> getDonationOrganizationByDonationHistoryIdx(@Param("donationHistoryIdx") Long donationHistoryIdx);


    // 기부내역 idx를 기준으로 기부내역 상세정보 조회 (DTO 객체 반환)
    @Query("SELECT new kr.co.leaf2u_api.donation.DonationHistoryDTO(" +
            "dh.idx, dh.donationAmount, dh.interest, dh.principal, dh.point, dh.donationDate, " +  // 후원 내역 Idx, 기부금액, 이자기부금, 원금기부금, 포인트기부금, 기부일
            "a.accountNumber, a.interestRate, a.primeRate, " +  // 계좌번호, 기본금리, 우대금리
            "(a.interestRate + a.primeRate) AS finalInterestRate )" +  // finalInterestRate 적용금리
            "FROM DonationHistory dh " +
            "JOIN dh.account a " +  // DonationHistory와 Account 조인
            "WHERE dh.idx = :donationHistoryIdx")  // 기부내역 idx 기준으로 조회
    Optional<DonationHistoryDTO> getDonationHistoryDetail(@Param("donationHistoryIdx") Long donationHistoryIdx);




}
