package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.account.AccountRepository;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.IntStream;

@SpringBootTest
public class CardRepositoryTests {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @Transactional
    @Commit
    public void insertCards() {

        IntStream.rangeClosed(1,5).forEach(i -> {

            Member member = memberRepository.findById((long)i).orElseThrow(()->new IllegalArgumentException("회원이 없습니다."));

            Card card = Card.builder()
                    .member(member)
                    .cardType('L')
                    .cardName("리프코드")
                    .cardNumber("#$#%^&%$")
                    .expirationDate("2025")
                    .balance(BigDecimal.ZERO)
                    .build();

            cardRepository.save(card);
        });
    }

}
