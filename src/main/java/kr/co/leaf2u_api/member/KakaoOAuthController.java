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
    public ResponseEntity<Void> kakaoCallback(@RequestParam("code") String code) {
        String jwtToken = kakaoOAuthService.kakaoLogin(code);

        // JWT 토큰이 유효한 경우 클라이언트에게 리다이렉트 URL을 반환
        if (jwtToken != null) {
            return ResponseEntity.status(302)  // 302 상태 코드: Found (리다이렉트)
                    .header("Location", "http://localhost:3000/start")  // 클라이언트가 리다이렉트될 URL
                    .header("Authorization", "Bearer " + jwtToken)  // 헤더에 JWT 토큰 추가
                    .build();
        } else {
            // JWT 토큰이 없으면 다른 적절한 처리를 할 수 있습니다.
            return ResponseEntity.status(400).build();  // 예: 400 Bad Request
        }
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