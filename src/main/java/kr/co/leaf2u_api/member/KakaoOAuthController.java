package kr.co.leaf2u_api.member;

import kr.co.leaf2u_api.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client.id}")
    private String CLIENT_ID;

    @Value("${kakao.redirect.uri}")
    private String REDIRECT_URI;


    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<Void> kakaoCallback(@RequestParam("code") String code) {

        String jwtToken = kakaoOAuthService.kakaoLogin(code);

        if (jwtToken != null) {
            // JWT 토큰을 쿼리 파라미터로 포함하여 리다이렉트
            String redirectUrl = "http://localhost:3000/?token=" + jwtToken;
            return ResponseEntity.status(302) // 302 상태 코드: Found (리다이렉트)
                    .header("Location", redirectUrl)
                    .build();
        } else {
            return ResponseEntity.status(400).build(); // 예: 400 Bad Request
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

    /**
     * jwtToken에서 이메일 받아오기
     */
    @GetMapping("/api/member-info")
    public ResponseEntity<Map<String, Object>> getMemberCardYn(@RequestHeader("Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            log.error("JWT 토큰이 제공되지 않았습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "인증 토큰이 필요합니다."));
        }

        log.info("전달 받은 토큰: {}", token);

        String jwtToken = token.substring(7);                     // "Bearer " 부분을 제거
        String email = jwtTokenProvider.getEmailFromToken(jwtToken);        // JwtTokenProvider에서 이메일 추출

        log.info("인증된 사용자 email: {}", email);

        try {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

            Map<String, Object> response = new HashMap<>();
            response.put("cardYn", member.getCardYn());
            response.put("firstYn", member.getSavingAccountYn());
            response.put("memberIdx",member.getIdx());

            log.info("member: {}", member);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회원 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "오류발생!!"));
        }
    }


}