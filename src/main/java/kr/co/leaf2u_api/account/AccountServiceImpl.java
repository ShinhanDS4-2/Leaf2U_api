package kr.co.leaf2u_api.account;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.card.CardRepository;
import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;

    @Transactional
    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {

        Member member = memberRepository.findById(accountDTO.getMemberIdx())
                .orElseThrow(()-> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Card card=cardRepository.findById(accountDTO.getMemberIdx())
                .orElseThrow(() -> new IllegalArgumentException("카드가 존재하지 않습니다."));

        List <Account> existAccounts=accountRepository.findByMemberIdx(member.getIdx());

        //기본 금리 설정
        BigDecimal baseInterestRate = new BigDecimal("1.0");
        BigDecimal primeRate = BigDecimal.ZERO;

        //최초 가입 시 1% 추가
        if(existAccounts.isEmpty()){
            primeRate=primeRate.add(new BigDecimal("1.0"));
        }

        //리프 카드 만들기를 선택한다면 2% 추가
        if (card.getCardType()=='L'){

            primeRate=primeRate.add(new BigDecimal("2.0"));
        }

        //기후 동행 카드를 만들기를 선택한다면 1% 추가
        else if (card.getCardType()=='E'){

            primeRate=primeRate.add(new BigDecimal("1.0"));             //발급받았으므로 'Y' 로 변경
        }

        //사용자 본인 카드를 선택한다면 0% 추가
        else if (card.getCardType()=='C'){

            primeRate=primeRate.add(new BigDecimal("0.0"));                                  //발급받았으므로 'Y' 로 변경
        }

        member.setCardYn('Y');
        memberRepository.save(member);

        Account account=new Account();

        account.setMember(member);
        account.setAccountStatus('N');
        account.setAccountNumber(generateAccountNumber());
        account.setAccountPassword(accountDTO.getAccountPassword());
        account.setBalance(BigDecimal.ZERO);
        account.setInterestRate(baseInterestRate);
        account.setPrimeRate(primeRate);
        account.setTaxationYn('Y');
        account.setMaturityDate(LocalDateTime.now().plusMonths(1));
        account.setInterestAmount(primeRate);

        Account savedAccount=accountRepository.save(account);
        return EntityToDTO(savedAccount);
    }

    private AccountDTO EntityToDTO(Account account) {

        AccountDTO dto=new AccountDTO();
        dto.setMemberIdx(account.getMember().getIdx());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setInterestRate(account.getInterestRate());
        dto.setPrimeRate(account.getPrimeRate());
        return dto;
    }

    /**
     * 계좌 번호 만들기 //앞 세 글자 222로 시작, 중간 세 글자, 뒤에 여섯 글자
     * @author 강현욱
     * @return
     */
    private String generateAccountNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        Random random = new Random();
        int last = random.nextInt(9000) + 1000; // 4자리 난수 생성
        return String.format("222-%s-%04d", timestamp, last);
    }

    /**
     * 사용자의 적금 현황 리스트
     * @author 강현욱
     * @param memberId
     * @return
     */
    @Override
    public List<AccountDTO> getAccountsByMember(Long memberId) {

        Member member=memberRepository.findById(memberId)
                .orElseThrow(()->new IllegalArgumentException("회원이 존재하지 않습니다."));

        List<Account> accounts=accountRepository.findByMemberIdx(member.getIdx());
        return accounts.stream().map(this::EntityToDTO).collect(Collectors.toList());
    }
}