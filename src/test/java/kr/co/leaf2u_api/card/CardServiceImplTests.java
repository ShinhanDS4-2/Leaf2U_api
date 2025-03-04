package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CardServiceImplTests {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private Member testMember;
    private Card testCard;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setIdx(1L);

        testCard = new Card();
        testCard.setMember(testMember);
        testCard.setCardType('L');
        testCard.setCardName("Leaf Card");
        testCard.setCardNumber("1234-5678-9101-1121");
        testCard.setExpirationDate("2028-12-31");
        testCard.setBalance(BigDecimal.ZERO);
    }

    @Test
    void testCreateLeafCard() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        CardDTO cardDTO = new CardDTO();
        cardDTO.setMemberIdx(1L);
        cardDTO.setCardType('L');
        cardDTO.setCardName("Leaf Card");
        cardDTO.setCardPassword("1234");

        CardDTO createdCard = cardService.createLeafCard(cardDTO);

        assertNotNull(createdCard);
        assertEquals("Leaf Card", createdCard.getCardName());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void testRegisterExistingCard() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        CardDTO cardDTO = new CardDTO();
        cardDTO.setMemberIdx(1L);
        cardDTO.setCardType('L');
        cardDTO.setCardName("Leaf Card");
        cardDTO.setCardNumber("1234-5678-9101-1121");
        cardDTO.setExpirationDate("2028-12-31");
        cardDTO.setBalance(BigDecimal.ZERO);

        CardDTO registeredCard = cardService.registerExistingCard(cardDTO);

        assertNotNull(registeredCard);
        assertEquals("1234-5678-9101-1121", registeredCard.getCardNumber());
        verify(cardRepository, times(1)).save(any(Card.class));
    }
}
