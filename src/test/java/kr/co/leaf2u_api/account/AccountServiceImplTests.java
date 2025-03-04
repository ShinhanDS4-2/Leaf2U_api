package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.card.CardRepository;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountServiceImplTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Member testMember;
    private Card testCard;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setIdx(1L);

        testCard = new Card();
        testCard.setCardType('L'); // 'L' 리프 카드

        testAccount = new Account();
        testAccount.setMember(testMember);
        testAccount.setAccountNumber("222-123-456789");
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setInterestRate(new BigDecimal("1.0"));
        testAccount.setPrimeRate(new BigDecimal("2.0"));
    }

    @Test
    void createAccount_Success() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setMemberIdx(1L);
        accountDTO.setAccountPassword("1234");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(accountRepository.findByMemberIdx(1L)).thenReturn(Collections.emptyList()); // 최초 가입
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        AccountDTO createdAccount = accountService.createAccount(accountDTO);

        assertNotNull(createdAccount);
        assertEquals("222-123-456789", createdAccount.getAccountNumber());
        assertEquals(new BigDecimal("1.0"), createdAccount.getInterestRate());
        assertEquals(new BigDecimal("2.0"), createdAccount.getPrimeRate());
    }

    @Test
    void getAccountsByMember_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(accountRepository.findByMemberIdx(1L)).thenReturn(List.of(testAccount));

        List<AccountDTO> accounts = accountService.getAccountsByMember(1L);

        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals("222-123-456789", accounts.get(0).getAccountNumber());
    }
}
