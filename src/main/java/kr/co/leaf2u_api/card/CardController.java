package kr.co.leaf2u_api.card;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
