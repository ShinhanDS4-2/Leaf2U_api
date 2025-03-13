package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findFirstByMemberIdxOrderByCreateDateDesc(Long memberIdx);



    // 사용중인 적금계좌에 연결된 카드(1개) 조회
}
