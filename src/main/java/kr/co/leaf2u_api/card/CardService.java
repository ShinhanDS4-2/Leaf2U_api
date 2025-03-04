package kr.co.leaf2u_api.card;

import java.util.Map;
import java.util.Optional;

public interface CardService {

    CardDTO createLeafCard(CardDTO cardDTO);
    CardDTO registerExistingCard(CardDTO cardDTO);

}
