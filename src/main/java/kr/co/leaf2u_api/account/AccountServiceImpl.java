package kr.co.leaf2u_api.account;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Account createAccount(AccountDTO accountDTO) {

        Member member = memberRepository.findById(accountDTO.getMemberId())
                .orElseThrow(()-> new IllegalArgumentException("회원이 존재하지 않습니다."));

        List <Account> existAccounts=accountRepository.findByMemberIdx(member.getIdx());

        //기본 금리 설정
        BigDecimal baseInterestRate = new BigDecimal("1.0");
        BigDecimal primeRate = BigDecimal.ZERO;

        //최초 가입 시 1% 추가
        if(existAccounts.isEmpty()){
            primeRate=primeRate.add(new BigDecimal("1.0"));
        }

        //자체 카드 만들기를 선택한다면 2% 추가  -> Card_yn이 N 일 경우 (후에 수정 필요)
        if (accountDTO.getCard_yn()){
            primeRate=primeRate.add(new BigDecimal("2.0"));
        }

        Account account=new Account();

        account.setMember(member);
        account.setAccount_status('N');
        account.setAccount_number(generateAccountNumber());
        account.setAccount_password(accountDTO.getAccountPassword());
        account.setBalance(BigDecimal.ZERO);
        account.setInterest_rate(baseInterestRate);
        account.setPrime_rate(primeRate);
        account.setTaxation_yn('N');
        account.setMaturity_date(LocalDateTime.now().plusMonths(1));
        account.setInterest_amount(primeRate);

        return accountRepository.save(account);
    }

    /**
     * 계좌 번호 만들기 //앞 세 글자 222로 시작, 중간 세 글자, 뒤에 여섯 글자
     * @author 강현욱
     * @return
     */
    private String generateAccountNumber() {

        Random random=new Random();
        int middle=random.nextInt(900)+100;
        int last=random.nextInt(9000000)+1000000;

        return String.format("222-%03d-%6d",middle,last);
    }

    /**
     * 사용자의 적금 현황 리스트
     * @author 강현욱
     * @param memberId
     * @return
     */
    @Override
    public List<Account> getAccountsByMember(Long memberId) {

        Member member=memberRepository.findById(memberId)
                .orElseThrow(()->new IllegalArgumentException("회원이 존재하지 않습니다."));

        return accountRepository.findByMemberIdx(member.getIdx());
    }
}
