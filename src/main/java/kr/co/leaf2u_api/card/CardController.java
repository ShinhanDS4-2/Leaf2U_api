package kr.co.leaf2u_api.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Log4j2
public class CardController {

    private final CardService cardService;

    /**
     * 리프 카드 등록
     * @param cardDTO
     * @return
     */
    @PostMapping("/new")
    public ResponseEntity<CardDTO> createLeafCard(@RequestBody CardDTO cardDTO) {

        CardDTO createdCard=cardService.createLeafCard(cardDTO);
        return ResponseEntity.ok(createdCard);
    }

    /**
     * 사용자 본인 카드 등록
     * @param cardDTO
     * @return
     */
    @PostMapping("/exist")
    public ResponseEntity<CardDTO> existCard(@RequestBody CardDTO cardDTO) {

        log.info("기존 카드 등록 카드 타입:{}", cardDTO.getCardType());

        CardDTO existingCard=cardService.registerExistingCard(cardDTO);
        return ResponseEntity.ok(existingCard);

    }

    @PostMapping("/card-info")
    public ResponseEntity<CardDTO> getCardInfo(@RequestBody CardDTO cardDTO) {

        Long memberIdx=cardDTO.getMemberIdx();
        log.info("멤버 idx: {}", memberIdx);

        CardDTO cardInfo=cardService.getCardInfo(memberIdx);

        log.info("카드 info:",cardInfo);
        return ResponseEntity.ok(cardInfo);

    }
}
