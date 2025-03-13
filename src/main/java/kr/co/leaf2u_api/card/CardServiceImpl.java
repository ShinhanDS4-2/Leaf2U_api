package kr.co.leaf2u_api.card;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.account.AccountDTO;
import kr.co.leaf2u_api.account.AccountRepository;
import kr.co.leaf2u_api.config.TokenContext;
import kr.co.leaf2u_api.donation.DonationHistoryDTO;
import kr.co.leaf2u_api.donation.DonationOrganizationDTO;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Notice;
import kr.co.leaf2u_api.member.MemberRepository;
import kr.co.leaf2u_api.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final NoticeService noticeService;

    //BCryptPasswordEncoder 인스턴스 생성
    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    @Transactional
    @Override
    public CardDTO createLeafCard(CardDTO cardDTO) {

        Member member=memberRepository.findById(cardDTO.getMemberIdx())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String hashedPassword = passwordEncoder.encode(cardDTO.getCardPassword());        //비밀번호 암호화

        Card card= Card.builder()
                .member(member)
                .cardType('L')
                .cardName(cardDTO.getCardName())
                .cardNumber(generateCardNumber())
                .accountNumber(cardDTO.getAccountNumber())
                .cardPassword(hashedPassword)
                .expirationDate(String.valueOf(LocalDateTime.now().plusYears(3)))
                .balance(BigDecimal.ZERO)
                .build();
        
        cardRepository.save(card);

        member.setCardYn('Y');
        memberRepository.save(member);

        /* 카드 발급 알림 insert - 문경미 */
        Map<String, Object> noticeParam = new HashMap<>();
        noticeParam.put("memberIdx", member.getIdx());
        noticeParam.put("title", "리프카드 발급 완료");
        noticeParam.put("content", "리프카드를 발급 받았습니다.");
        noticeParam.put("category", "C");

        noticeService.registNotice(noticeParam);

        return entityToDTO(card);
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
    public CardDTO registerExistingCard(CardDTO cardDTO) {

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
        return entityToDTO(card);
    }

    @Override
    public CardDTO getCardInfo(Long memberIdx) {

        Card cardInfo =cardRepository.findFirstByMemberIdxOrderByCreateDateDesc(memberIdx).orElse(null);

        return entityToDTO(cardInfo);
    }


    private CardDTO entityToDTO(Card card) {

        CardDTO dto=new CardDTO();
        dto.setMemberIdx(card.getMember().getIdx());
        dto.setCardType(card.getCardType());
        dto.setCardName(card.getCardName());
        dto.setCardNumber(card.getCardNumber());
        dto.setAccountNumber(card.getAccountNumber());
        dto.setExpirationDate(card.getExpirationDate());
        dto.setBalance(card.getBalance());

        return dto;
    }


}
