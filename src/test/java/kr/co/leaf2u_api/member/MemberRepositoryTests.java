package kr.co.leaf2u_api.member;

import kr.co.leaf2u_api.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void insertMembers() {

        IntStream.rangeClosed(1,5).forEach(i->{

            Member member= Member.builder()
                    .email("xxx"+i+"@kakao.com")
                    .pattern_password("temporal")
                    .name("user"+i)
                    .phone_number("010-1234-1234")
                    .birthday("1996-09-20")
                    .gender("man")
                    .savingAccountYn('N')
                    .card_yn('N')
                    .build();

            memberRepository.save(member);
        });
    }
}
