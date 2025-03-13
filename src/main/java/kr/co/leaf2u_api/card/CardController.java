package kr.co.leaf2u_api.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    /**
     * 리프 카드 등록
     * @param cardDTO
     * @return
     */
    @PostMapping("/new")
    public ResponseEntity<CardDTO> createLeafCard(@RequestBody CardDTO cardDTO) {

        log.info("전송받은 memberIdx: {}", cardDTO.getMemberIdx());
        log.info("전송받은 비밀번호: {}", cardDTO.getCardPassword());
        log.info("전송받은 계좌번호: {}", cardDTO.getAccountNumber());

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
