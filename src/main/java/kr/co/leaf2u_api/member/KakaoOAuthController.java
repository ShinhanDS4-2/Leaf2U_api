package kr.co.leaf2u_api.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @Value("${kakao.client.id}")
    private String CLIENT_ID;

    @Value("${kakao.redirect.uri}")
    private String REDIRECT_URI;

    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam("code") String code) {
        String jwtToken = kakaoOAuthService.kakaoLogin(code);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwtToken)
                .body("Kakao login successful");
    }

    // Kakao 인증 URL 제공
    @GetMapping("/auth/kakao/login-url")
    public ResponseEntity<String> getKakaoLoginUrl() {
        String kakaoAuthUrl = String.format(
                "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                CLIENT_ID, REDIRECT_URI);
        return ResponseEntity.ok(kakaoAuthUrl);
    }
}