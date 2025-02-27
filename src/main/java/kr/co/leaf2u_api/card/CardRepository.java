package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByMemberIdx(Long memberIdx);
}
