package kr.co.leaf2u_api.repository;

import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.member.MemberRepository;
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
                    .build();

            memberRepository.save(member);
        });
    }
}
