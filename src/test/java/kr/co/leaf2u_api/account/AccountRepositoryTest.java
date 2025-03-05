package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void insertAccounts(){

        IntStream.rangeClosed(1,5).forEach(i->{

            Account account= Account.builder()
                    .member(memberRepository.findById((long)i).orElseThrow(()->new IllegalArgumentException("회원이 존재하지 않습니다.")))
                    .accountStatus('N')
                    .accountNumber("222-3456-8766")
                    .accountPassword("654321")
                    .balance(BigDecimal.ZERO)
                    .interestRate(BigDecimal.ONE)
                    .primeRate(BigDecimal.ZERO)
                    .taxationYn('Y')
                    .paymentAmount(BigDecimal.valueOf(10000))
                    .maturityDate(LocalDateTime.now().plusMonths(1))
                    .build();

            accountRepository.save(account);
        });

    }
}
