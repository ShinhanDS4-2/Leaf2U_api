package kr.co.leaf2u_api.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leaf2u_api.entity.Member;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoOAuthService.class);
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.client.id}")
    private String CLIENT_ID;

    @Value("${kakao.redirect.uri}")
    private String REDIRECT_URI;

    public String kakaoLogin(String code) {
        // 1. 액세스 토큰 발급
        String accessToken = getAccessToken(code);
        log.info("Kakao Access Token: {}", accessToken);

        // 2. 사용자 정보 가져오기
        KakaoUserInfo userInfo = getUserInfo(accessToken);
        log.info("Kakao User Info - ID: {}, Email: {}, Nickname: {} ,birthyear: {}, birthday: {}, phone_number: {}, Gender: {}",
                userInfo.getKakaoId(), userInfo.getEmail(), userInfo.getNickname(),userInfo.getBirthyear(), userInfo.getBirthday(),userInfo.getPhone_number(),userInfo.getGender());

        // 3. DB에서 사용자 조회 또는 등록
        Member member = registerOrGetMember(userInfo);

        // 4. JWT 토큰 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail());
        log.info("Generated JWT Token: {}", jwtToken);

        return jwtToken;
    }

    private Member registerOrGetMember(KakaoUserInfo userInfo) {

        Optional<Member> existingMember=memberRepository.findByEmail(userInfo.getEmail());

        if (existingMember.isPresent()) {
            return existingMember.get();
        } else {
            String nickname = userInfo.getNickname() != null ? userInfo.getNickname() : "KakaoUser";
            String email = userInfo.getEmail() != null ? userInfo.getEmail() : "@kakao.com";
            String gender=userInfo.getGender();
            String birthyear= userInfo.getBirthyear();
            String birthday= userInfo.getBirthday();
            String phone=userInfo.getPhone_number();

            Member newMember = Member.builder()
                    .email(email)  // 무조건 넘어온 값 또는 kakaoId 기반 값 사용
                    .patternPassword("kakao_default_password")
                    .name(nickname)
                    .phoneNumber(phone)
                    .birthday(birthyear+'-'+birthday)
                    .gender(gender)
                    .savingAccountYn('N')
                    .cardYn('N')
                    .build();
            return memberRepository.save(newMember);
        }
    }

    private String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        String requestBody = String.format(
                "grant_type=authorization_code&client_id=%s&redirect_uri=%s&code=%s",
                CLIENT_ID, REDIRECT_URI, code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            } catch (Exception e) {
                log.error("Failed to parse access token response: {}", response.getBody(), e);
                throw new RuntimeException("Failed to parse Kakao token response", e);
            }
        } else {
            log.error("Failed to get access token: {}", response.getBody());
            throw new RuntimeException("Failed to get Kakao access token");
        }
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                KakaoUserInfo userInfo = objectMapper.treeToValue(jsonNode, KakaoUserInfo.class);
                return userInfo;
            } catch (Exception e) {
                log.error("Failed to parse user info response: {}", response.getBody(), e);
                throw new RuntimeException("Failed to parse Kakao user info", e);
            }
        } else {
            log.error("Failed to get user info: {}", response.getBody());
            throw new RuntimeException("Failed to get Kakao user info");
        }
    }
}
