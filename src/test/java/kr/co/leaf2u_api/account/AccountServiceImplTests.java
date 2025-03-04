package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.card.CardRepository;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AccountServiceImplTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Member member;
    private Card card;
    private AccountDTO accountDTO;

    @BeforeEach
    public void setUp() {
        member = new Member();
        member.setIdx(1L);
        member.setCardYn('N');
        member.setName("John Doe");

        card=new Card();
        card.setCardType('L');

        accountDTO=new AccountDTO();
        accountDTO.setMemberIdx(1L);
        accountDTO.setAccountPassword("password123");

    }



}
