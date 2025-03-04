package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.donation.DonationOrganizationDTO;
import kr.co.leaf2u_api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByMemberIdx(Long memberIdx);            //사용자가 갖고 있는 모든 카드 조회


    // 사용자의 카드 정보 조회
    @Query("SELECT c FROM Card c WHERE c.member.idx = :memberIdx")
    Optional<Card> getCardInfo(@Param("memberIdx") Long memberIdx);
}
