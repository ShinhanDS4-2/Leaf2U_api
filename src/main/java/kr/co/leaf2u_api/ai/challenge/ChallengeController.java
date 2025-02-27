package kr.co.leaf2u_api.ai.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping("/upload")
    public ResponseEntity<ChallengeResponseDto> upload(@RequestParam("image") MultipartFile image) {

        ChallengeResponseDto response = challengeService.analyzeImage(image);
        return ResponseEntity.ok(response);
    }

}
