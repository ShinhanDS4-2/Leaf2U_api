package kr.co.leaf2u_api.card;

public interface CardService {

    CardDTO createLeafCard(CardDTO cardDTO);
    CardDTO registerExistingCard(CardDTO cardDTO);

}
