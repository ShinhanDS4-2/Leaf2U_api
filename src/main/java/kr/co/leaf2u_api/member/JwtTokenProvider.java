package kr.co.leaf2u_api.member;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long expiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); // Base64 인코딩된 키 변환
        this.expiration = expiration;
    }

    /**
     * JWT 토큰 생성
     */
    public String createToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWT 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT Token입니다.");
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 형식의 JWT Token입니다.");
        } catch (SignatureException e) {
            System.out.println("JWT 서명이 유효하지 않습니다.");
        } catch (Exception e) {
            System.out.println("JWT 검증 중 알 수 없는 오류 발생: " + e.getMessage());
        }
        return false;
    }
}
