package kr.co.leaf2u_api.account;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.card.CardRepository;
import kr.co.leaf2u_api.entity.Card;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 및 검증을 위한 인코더

    @Transactional
    @Override
    public Account createAccount(AccountDTO accountDTO) {

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

        //최초로 리프 카드 만들기를 선택한다면 2% 추가
        if (member.getCardYn()=='N' && card.getCardType()=='L'){

            primeRate=primeRate.add(new BigDecimal("2.0"));
            member.setCardYn('Y');
            memberRepository.save(member);                                  //발급받았으므로 'Y' 로 변경
        }

        //처음에 기존 사용자로 만들었고 두 번째 만들 때 리프카드를 만드는 경우
        if(member.getCardYn()=='Y' && card.getCardType()=='L'){

            primeRate=primeRate.add(new BigDecimal("2.0"));
        }

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


/* 적금 계좌 관리 - 시온 */
    /** (1) 계좌 기본 정보 조회
     * @param MEMBER(사용자) idx
     * @return account 엔티티
     */
    @Override
    public Account getAccountInfoById(Long memberIdx) {
        Optional<Account> accountInfo = accountRepository.getAccountInfoById(memberIdx);
        Account account = accountInfo.orElseThrow(() -> new NoSuchElementException("해당 회원의 계좌 정보를 찾을 수 없습니다."));

        // 원래 DTO로 변환해서 반환해야하는데 ........
        return account;
    }

    /** (2) 납입금액 변경 
     * @param AccountDTO(Idx, accountPassword, paymentAmount)
     * @return 1(성공), 0(실패), 401(비밀번호 불일치)
     */
    // 클라이언트로부터 납입금액, 인증 비밀번호6자리 입력받음. (비밀번호 일치 시에만 로직 수행되도록)
    @Override
    public int updatePaymentAmount(AccountDTO accountDTO) {
        Long idx = accountDTO.getIdx();  // 계좌 idx
        String inputPwd = accountDTO.getAccountPassword();  // 사용자가 입력한 계좌 비밀번호
        BigDecimal inputAmount = accountDTO.getPaymentAmount();  // 사용자가 입력한 납입금액

        // 해당 적금계좌의 현재 비밀번호를 DB에서 조회
        Account account = accountRepository.findByIdx(idx)  // idx 기준으로 Account 엔티티 조회
                .orElseThrow(() -> new IllegalArgumentException("계좌정보가 존재하지 않음"));

        // 비밀번호 일치하는지 확인
        // PasswordEncoder : 비밀번호의 암호와 및 검증을 담당하는 Spring Security 컴포넌트
        if(!passwordEncoder.matches(inputPwd, account.getAccountPassword())){
                    // ㄴ 사용자가 입력한 비밀번호가 DB에 저장된 비밀번호와 일치하는지 확인
            return 401;  // 비밀번호 불일치시 401반환
        }
        /** passwordEncoder.matches는 입력된 비밀번호를 해시하여 DB에 저장된 암호화된 비밀번호와 비교한다.
         * 즉, 암호화된 상태에서 비교가 이루어지고, 비밀번호가 일치하면 true를 반환한다.  */
        
        // 비밀번호가 일치하면 납입금액 업데이트
        account.setPaymentAmount(inputAmount);  // Account엔티티의 paymentAmount필드를 사용자가 입력한 값으로 업데이트
        Account updatedAccount = accountRepository.save(account);  // paymentAmount필드가 변경된 account엔티티를 DB에 저장
        // DB 업데이트 되면서 수정일 컬럼도 자동으로 업데이트 됨. 따로 설정 필요없음

    // save() 메서드는 반환 값으로 저장된 엔티티를 반환함 => 반환된 엔티티가 null이 아니면 업데이트 성공
        if(updatedAccount != null){
            return 1;  // 성공 시 1 반환
        } else {
            return 0;  // 실패 시 0 반환
        }
    }

    /** (3) 예상 이자 조회 - 1만기일해지
     * @param AccountDTO(idx)
     * @return Account(적금계좌), InterestRateHistory(금리내역)
     */
    @Override
    public Map<String, Object> getMaturityInterest(AccountDTO accountDTO) {
        Long idx = accountDTO.getIdx();  // 계좌 idx
        Account account = accountRepository.findByIdx(idx)  // 계좌 idx 기준으로 Account 엔티티 반환
                // 계좌 idx 기준으로 List<InterestRateHistory금리내역> 엔티티 반환

                .orElseThrow(() -> new IllegalArgumentException("계좌정보가 존재하지 않음"));

        return Map.of();
    }

    /** (4) 계좌 해지
     * @param AccountDTO(Idx->계좌idx, accountPassword->계좌 비밀번호)
     * @return 1(성공), 0(실패), 401(비밀번호 불일치)
     */
    @Override
    public int terminateAccount(AccountDTO accountDTO) {
        // 비밀번호 확인하고

        // 맞으면 해지 완료
        // --> 이거 납입금액 변경이랑 로직이 거의 비슷함 내일 다시 해볼 것
        return 1;
    }


}
