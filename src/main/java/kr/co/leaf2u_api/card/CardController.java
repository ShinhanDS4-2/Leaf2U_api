package kr.co.leaf2u_api.card;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/new")
    public ResponseEntity<Map<String,Object>> createLeafCard(@RequestBody CardDTO cardDTO) {

        Map<String ,Object> response=cardService.createLeafCard(cardDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exist")
    public ResponseEntity<Map<String,Object>> existCard(@RequestBody CardDTO cardDTO) {

        Map<String,Object> response=cardService.registerExistingCard(cardDTO);
        return ResponseEntity.ok(response);

    }

    /** 자동이체 카드정보 조회
     * @param
     * @return CardDTO
     */
    @GetMapping("/info")
    public ResponseEntity<Optional<CardDTO>> getCardInfo(@RequestBody Long memberIdx) {
        Optional<CardDTO> cardInfo = cardService.getCardInfo(memberIdx);
        return ResponseEntity.ok(cardInfo);

    }

}
