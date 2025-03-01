package kr.co.leaf2u_api.card;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
}
