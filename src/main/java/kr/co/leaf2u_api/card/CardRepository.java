package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Modifying
    @Transactional
    @Query("UPDATE Card c SET c.balance = :balance WHERE c.idx = :idx")
    int updateBalance(Long idx, BigDecimal balance);

    @Query("""
        SELECT COUNT(c)
        FROM Card c
        WHERE c.cardNumber = :cardNum
        AND c.cardType = 'L'
        AND c.member.idx = :memberIdx
    """)
    int findPrevCard(@Param("memberIdx") Long memberIdx, @Param("cardNum") String cardNum);
}