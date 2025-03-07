package kr.co.leaf2u_api.donation;

import kr.co.leaf2u_api.entity.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Long> {
/** 후원 관리 - 시온 */
    // (1) 후원내역 리스트 반환 (DTO 객체 반환)
    @Query("SELECT new kr.co.leaf2u_api.donation.DonationHistoryDTO(dh.idx, do.name, sa.accountNumber, dh.donationAmount, dh.donationDate) " +
            "FROM DonationHistory dh " +  // 데이터를 조회할 주 엔티티 지정
            "JOIN dh.donationOrganization do " +  // DonationOrganization 엔티티 자동조인
            "JOIN dh.account sa " +  // Account 엔티티 자동조인
            "WHERE dh.member.idx = :memberIdx") // idx: Member 엔티티의 기본 키(primary key)를 의미
    List<DonationHistoryDTO> getDonationHistoryList(@Param("memberIdx") Long memberIdx);


    // (2) 후원내역 리스트 개수 반환
    @Query("SELECT COUNT(dh) FROM DonationHistory dh WHERE dh.member.idx = :memberIdx")
    long countByDonationHistoryList(@Param("memberIdx") Long memberIdx);


    // (3) donationHistoryIdx 받아서 후원내역 상세정보(기부처, 기부내역) 반환하는 JPQL 쿼리
    // 기부내역 idx를 기준으로 기부처 정보 조회 (DTO 객체 반환) => 얘는 기본생성자 사용하는거라 DTO에서 별도 생성자 지정안해도댐
    @Query("SELECT new kr.co.leaf2u_api.donation.DonationOrganizationDTO(" +
            "do.idx, " +  // 기부처 ID
            "do.name, " + // 기부처 이름
            "do.telNumber, " + // 기부처 전화번호
            "do.description) " + // 기부처 설명
            "FROM DonationOrganization do " +
            "JOIN DonationHistory dh ON dh.donationOrganization.idx = do.idx " + // 기부내역과 기부처 조인
            "WHERE dh.idx = :donationHistoryIdx")  // 기부내역 idx 기준으로 조회
    Optional<DonationOrganizationDTO> getDonationOrganization(@Param("donationHistoryIdx") Long donationHistoryIdx);


    // (4) 기부내역 idx를 기준으로 기부내역 상세정보 조회 (DTO 객체 반환)
    @Query("SELECT new kr.co.leaf2u_api.donation.DonationHistoryDTO(" +
            "dh.idx, dh.donationAmount, dh.interest, dh.principal, dh.point, dh.donationDate, " +  // 후원 내역 Idx, 기부금액, 이자기부금, 원금기부금, 포인트기부금, 기부일
            "a.accountNumber, a.interestRate, a.primeRate, a.finalInterestRate )" +  // 계좌번호, 기본금리, 우대금리, 최종금리
            "FROM DonationHistory dh " +
            "JOIN dh.account a " +  // DonationHistory와 Account 조인
            "WHERE dh.idx = :donationHistoryIdx")  // 기부내역 idx 기준으로 조회
    Optional<DonationHistoryDTO> getDonationHistoryDetail(@Param("donationHistoryIdx") Long donationHistoryIdx);


    /**
     * 후원 기여도 금액
     * @param memberIdx
     * @return
     */
    @Query(value = """
        SELECT
            COALESCE(SUM(dh.donation_amount), 0) AS all_total,
            (
                SELECT 
                    COALESCE(SUM(age_donations.donation_sum), 0) AS age_total
                FROM (
                    SELECT dh2.member_idx, SUM(dh2.donation_amount) AS donation_sum
                    FROM donation_history dh2
                    WHERE dh2.member_idx IN (
                        SELECT 
                            m.idx
                        FROM member m
                        WHERE 
                            STR_TO_DATE(m.birthday, '%Y-%m-%d') 
                        BETWEEN 
                            DATE_SUB(CURDATE(), INTERVAL (TIMESTAMPDIFF(YEAR, STR_TO_DATE((SELECT m2.birthday FROM member m2 WHERE m2.idx = :memberIdx), '%Y-%m-%d'), CURDATE()) + 5) YEAR) 
                        AND DATE_SUB(CURDATE(), INTERVAL (TIMESTAMPDIFF(YEAR, STR_TO_DATE((SELECT m2.birthday FROM member m2 WHERE m2.idx = :memberIdx), '%Y-%m-%d'), CURDATE()) - 5) YEAR)
                        AND m.idx != :memberIdx
                    )
                    GROUP BY dh2.member_idx
                ) AS age_donations
            ) AS age_total,
            (
                SELECT 
                    COALESCE(SUM(dh3.donation_amount), 0) AS my_total
                FROM donation_history dh3
                WHERE dh3.member_idx = :memberIdx
            ) AS my_total
        FROM donation_history dh
    """, nativeQuery = true)
    Map<String, Object> findDonationSums(@Param("memberIdx") Long memberIdx);

    /**
     * 후원금 랭킹 3위
     * @return
     */
    @Query(value = """
        SELECT
            t.member_idx,
            m.name,
            t.total_donation,
            t.ranking
        FROM (
            SELECT
                dh.member_idx,
                SUM(dh.donation_amount) AS total_donation,
                ROW_NUMBER() OVER (ORDER BY SUM(dh.donation_amount) DESC) AS ranking
            FROM donation_history dh
            GROUP BY dh.member_idx
        ) AS t
        JOIN member m
        ON m.idx = t.member_idx
        WHERE ranking <= 3
        ;
    """, nativeQuery = true)
    List<Map<String, Object>> findDonationRanking();

}
