package kr.co.leaf2u_api.card;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.donation.DonationHistoryDTO;
import kr.co.leaf2u_api.donation.DonationOrganizationDTO;
import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Map<String, Object> createLeafCard(CardDTO cardDTO) {

        Member member=memberRepository.findById(cardDTO.getMemberIdx())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Card card= Card.builder()
                .member(member)
                .cardType(cardDTO.getCardType())
                .cardName(cardDTO.getCardName())
                .cardNumber(generateCardNumber())
                .cardPassword(cardDTO.getCardPassword())
                .expirationDate(String.valueOf(LocalDateTime.now().plusYears(3)))
                .balance(BigDecimal.ZERO)
                .build();
        
        cardRepository.save(card);

        Map<String,Object> response=new HashMap<>();
        response.put("message","Leaf 카드 발급 완료");
        response.put("cardId",card.getIdx());
        return response;
    }

    private String generateCardNumber() {

        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int randomNumber = random.nextInt(9000) + 1000; // 1000~9999 사이의 랜덤 4자리 숫자
            cardNumber.append(randomNumber);
            if (i < 3) {
                cardNumber.append("-");
            }
        }
        return cardNumber.toString();
    }

    @Transactional
    @Override
    public Map<String, Object> registerExistingCard(CardDTO cardDTO) {

        Member member=memberRepository.findById(cardDTO.getMemberIdx())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Card card=Card.builder()
                .member(member)
                .cardType(cardDTO.getCardType())
                .cardName(cardDTO.getCardName())
                .cardNumber(cardDTO.getCardNumber())
                .expirationDate(cardDTO.getExpirationDate())
                .balance(cardDTO.getBalance())
                .build();

        cardRepository.save(card);

        Map<String,Object> response=new HashMap<>();
        response.put("message","사용자 기존 카드 등록 완료");
        response.put("cardId",card.getIdx());
        return response;
    }

    /** 자동이체 카드정보 조회
     * @param MEMBER(사용자) idx
     * @return CardDTO
     */
    @Override
    public Optional<CardDTO> getCardInfo(Long memberIdx) {
        Optional<Card> cardInfo = cardRepository.getCardInfo(memberIdx);  // 카드 엔티티
        return cardInfo.map(card -> new CardDTO(card.getCardNumber(), card.getExpirationDate(), card.getCardName()));
    }
}
