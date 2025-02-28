package kr.co.leaf2u_api.card;

import java.util.Map;

public interface CardService {

    Map<String,Object> createLeafCard(CardDTO cardDTO);
    Map<String,Object> registerExistingCard(CardDTO cardDTO);
}
