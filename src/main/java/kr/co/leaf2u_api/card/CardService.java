package kr.co.leaf2u_api.card;

import java.util.Map;

public interface CardService {

    CardDTO createLeafCard(CardDTO cardDTO);
    CardDTO registerExistingCard(CardDTO cardDTO);
    CardDTO getCardInfo(Long memberIdx);
    /** 03/13추가 - 시온 */
    Map<String, Object> CardInfo();  // 계좌에 연결 된 카드정보 조회 (1개)


}
