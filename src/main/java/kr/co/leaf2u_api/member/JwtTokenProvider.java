package kr.co.leaf2u_api.member;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final Key secretKey;
    private final long expiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);                            // Base64 디코딩
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    /**
     * JWT 토큰 생성
     * @param email
     * @return
     */
    public String createToken(String email) {

        Date now=new Date();
        Date expiryDate=new Date(now.getTime()+expiration);

        log.info("토큰 생성 시간:{}",now);
        log.info("토큰 만료 시간:{}",expiryDate);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey) // SignatureAlgorithm 제거
                .compact();
    }

    /**
     * request에서 토큰 추출
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }


    /**
     * 토큰에서 이메일 추출
     * @param token
     * @return
     */
    public String getEmailFromToken(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(10)  // 10초 허용
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰: {}", e.getClaims().getExpiration());
            log.error("현재 서버 시간: {}", new Date());
            return null;
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return null;
        }
    }


    /**
     * JWT 토큰 유효성 검사
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT Token입니다.");
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT Token입니다.");
        } catch (SignatureException e) {
            log.warn("JWT 서명이 유효하지 않습니다.");
        } catch (Exception e) {
            log.error("JWT 검증 중 알 수 없는 오류 발생: {}", e.getMessage());
        }
        return false;
    }
}
