package kr.co.leaf2u_api.member;

import kr.co.leaf2u_api.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KakaoOAuthServiceTest {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testJwtTokenGeneration() {
        // Given
        Member member = Member.builder()
                .email("test@kakao.com")
                .name("Test User")
                .phoneNumber("010-1234-5678")
                .birthday("1990-01-01")
                .gender("male")
                .savingAccountYn('N')
                .cardYn('N')
                .build();
        memberRepository.save(member);

        // When
        String jwtToken = jwtTokenProvider.createToken(member.getEmail());

        // Then
        assertThat(jwtToken).isNotNull();
        assertThat(jwtTokenProvider.validateToken(jwtToken)).isTrue();
        assertThat(jwtTokenProvider.getEmailFromToken(jwtToken)).isEqualTo(member.getEmail());
    }

    @Test
    void testMemberRegistration() {
        // Given
        String email = "newuser@kakao.com";

        // When
        Member newMember = Member.builder()
                .email(email)
                .name("New Kakao User")
                .phoneNumber("010-5555-8888")
                .birthday("2000-12-31")
                .gender("female")
                .savingAccountYn('Y')
                .build();
        memberRepository.save(newMember);

        // Then
        Optional<Member> retrievedMember = memberRepository.findById(newMember.getIdx());
        assertThat(retrievedMember).isPresent();
        assertThat(retrievedMember.get().getEmail()).isEqualTo(email);
    }
}
