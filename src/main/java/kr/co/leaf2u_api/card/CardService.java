package kr.co.leaf2u_api.card;

import java.util.Map;

public interface CardService {

    CardDTO createLeafCard(CardDTO cardDTO);
    CardDTO registerExistingCard(CardDTO cardDTO);
    CardDTO getCardInfo(Long memberIdx);


}
