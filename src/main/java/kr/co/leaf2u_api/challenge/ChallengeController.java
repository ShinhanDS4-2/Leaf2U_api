package kr.co.leaf2u_api.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping("/upload")
    public ResponseEntity<ChallengeResponseDto> upload(@RequestParam("image") MultipartFile image) {
        ChallengeRequestDto requestDto = new ChallengeRequestDto();
        requestDto.setImage(image);

        ChallengeResponseDto responseDto = challengeService.analyzeImage(requestDto);
        return ResponseEntity.ok(responseDto);
    }

}
