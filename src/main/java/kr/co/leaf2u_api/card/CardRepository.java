package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;


public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findFirstByMemberIdxOrderByCreateDateDesc(Long memberIdx);

    // 사용중인 적금계좌에 연결된 카드(1개) 조회
    @Query("""
        SELECT c 
        FROM Card c JOIN AccountCard ac ON c.idx = ac.card.idx
        WHERE ac.card.idx = (
            SELECT ac2.card.idx FROM AccountCard ac2 WHERE ac2.account.idx = :accountIdx
        )
        """)
    Optional<Card> findCardInfoByAccountIdx(@Param("accountIdx") Long accountIdx);
}