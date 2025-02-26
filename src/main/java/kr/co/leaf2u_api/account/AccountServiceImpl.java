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

        //자체 카드 만들기를 선택한다면 2% 추가
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

    private String generateAccountNumber() {

        //앞 세 글자, 셋, 여섯, 앞에 세 글자는  222-랜덤-랜덤
        return null;

    }

    @Override
    public List<Account> getAccountsByMember(Long memberId) {

        return List.of();
    }
}
