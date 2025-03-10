package kr.co.leaf2u_api.account;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.card.CardRepository;
import kr.co.leaf2u_api.config.TokenContext;
import kr.co.leaf2u_api.donation.DonationHistoryRepository;
import kr.co.leaf2u_api.entity.*;
import kr.co.leaf2u_api.member.MemberRepository;
import kr.co.leaf2u_api.notice.NoticeService;
import kr.co.leaf2u_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.resizers.BicubicResizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final DonationHistoryRepository donationHistoryRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 및 검증을 위한 인코더

    private final NoticeService noticeService;

    /**
     * 적금 계좌 생성
     * @param accountDTO
     * @return
     */
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

        account.setDutyRate(new BigDecimal("15.4"));
        account.setFinalInterestRate(baseInterestRate.add(primeRate));
        account.setPaymentAmount(accountDTO.getPaymentAmount());
        account.setSavingCnt(0L);

        Account savedAccount=accountRepository.save(account);

        /**
         * TODO account_card, interest_rate_history insert
         */


        /* 적급 가입 알림 insert - 문경미 */
        Map<String, Object> noticeParam = new HashMap<>();
        noticeParam.put("memberIdx", member.getIdx());
        noticeParam.put("title", "한달적금 개설 완료");
        noticeParam.put("content", "한달적금이 개설되었습니다. 지금 바로 입금하고 우대금리 받아보세요!");
        noticeParam.put("category", "O");

        noticeService.registNotice(noticeParam);

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
     * 계좌 번호 만들기 //앞 세 글자 222로 시작, 날짜 + 랜덤번호
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


/** 적금 계좌 관리 - 시온 */
    /** (1) 계좌 기본 정보 조회
     * @param memberIdx
     * @return AccountDTO
     */
    @Override
    public Map<String, Object> getAccountInfoById(Long memberIdx) {
        Account account = accountRepository.getAccountInfoByIdx(memberIdx).orElse(null);

        System.out.println(account);

        /** (엔티티 -> DTO 변환) 공통 메서드 사용 */
        AccountDTO dto = entityToDTO(account);

        Map<String, Object> result = new HashMap<>();
        result.put("accountDTO", dto);  // 계좌 DTO
        return result;
    }

    /** (2) 납입금액 변경
     * @param accountDTO
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
//      if(!passwordEncoder.matches(inputPwd, account.getAccountPassword())){
        if(!inputPwd.equals(account.getAccountPassword())){  // API테스트용 임시 코드(위에거로 변경해야함)
            // ㄴ 사용자가 입력한 비밀번호가 DB에 저장된 비밀번호와 일치하는지 확인
            return 401;  // 비밀번호 불일치시 401반환
        }
        /** passwordEncoder.matches는 입력된 비밀번호를 해시하여 DB에 저장된 암호화된 비밀번호와 비교한다.
         * 즉, 암호화된 상태에서 비교가 이루어지고, 비밀번호가 일치하면 true를 반환한다.  */

        // 비밀번호가 일치하면 납입금액 업데이트
        account.setPaymentAmount(inputAmount);  // Account엔티티의 paymentAmount필드를 사용자가 입력한 값으로 업데이트
        Account updatedAccount = accountRepository.save(account);  // paymentAmount필드가 변경된 account엔티티를 DB에 저장
        // DB 업데이트 되면서 수정일 컬럼도 자동으로 업데이트 됨. 따로 설정 필요없음

        // save() 메서드는 반환 값으로 저장된(업데이트된) 엔티티를 반환함 => 반환된 엔티티가 null이 아니면 업데이트 성공
        if(updatedAccount != null){
            return 1;  // 성공 시 1 반환
        } else {
            return 0;  // 실패 시 0 반환
        }
    }

    /** (3-1) 예상이자조회 - 만기일해지
     * @param accountIdx
     * @return accountDTO(적금계좌), interestRateHistory(금리내역)
     */
    @Override
    public Map<String, Object> getMaturityInterest(Long accountIdx) {
        // 계좌 idx 기준으로 List<InterestRateHistory금리내역> 엔티티 반환
        List<InterestRateHistory> interestRateHistory= accountRepository.getInterestRateHistory(accountIdx);  // 금리내역
        Account account = accountRepository.findByIdx(accountIdx).orElse(null);

        LocalDateTime maturityDate = account.getMaturityDate();  // account엔티티에서 적금만기일 가져오기
        BigDecimal finalInterestRate = account.getFinalInterestRate().multiply(new BigDecimal("0.01"));  // 최종금리 2.7% 형식으로 저장되어 있어서 * 0.01 으로 0.027로 변환해야함

        /** 이자 계산(만기일 해지) START */
        // 이자 계산 공통 메서드(idx, 적용금리:최종금리, 해지일:만기일)
        AccountDTO dto = calculateInterest(accountIdx, finalInterestRate, maturityDate);
        /** 이자 계산(만기일 해지) END */

        Map<String, Object> result = new HashMap<>();
        result.put("accountDTO", dto);  // 계좌 DTO
        result.put("interestRateHistory", interestRateHistory);  // 금리내역

        return result;
    }

    /** (3-2) 예상이자조회 - 오늘해지 (우대금리X)
     * @param accountIdx
     * @return accountDTO(적금계좌)
     */
    @Override
    public Map<String, Object> getTodayInterest(Long accountIdx) {
        Account account = accountRepository.findByIdx(accountIdx).orElse(null);

        LocalDateTime today = LocalDateTime.now();  // 오늘날짜
        LocalDateTime maturityDate = account.getMaturityDate();  // 적금만기일
        BigDecimal interestRate = account.getInterestRate().multiply(new BigDecimal("0.01"));;  // 기본금리 1.0%로 저장되어 있음. 0.01로 변환

        if(today.toLocalDate().equals(maturityDate.toLocalDate())) {  // 오늘날짜가 적금만기일이면 getMaturityInterest() -> 만기일이자조회 메서드 실행
            // .toLocalDate() 이용해서 날짜만 비교(시간X)
            return getMaturityInterest(accountIdx);
        }

        /** 이자 계산(만기일 해지) START */
        // 이자 계산 공통 메서드(idx, 적용금리:기본금리, 해지일:오늘)
        AccountDTO dto = calculateInterest(accountIdx, interestRate, today);
        /** 이자 계산(만기일 해지) END */

        Map<String, Object> result = new HashMap<>();
        result.put("accountDTO", dto);  // 계좌 DTO
        return result;

    }

    /** (3-3) 예상이자조회 - 선택일자 해지 (우대금리X)
     * @param accountDTO idx, endDate(이자 계산기간 종료일 - 선택일자 해지에 필요)
     * @return accountDTO(적금계좌)
     */
    @Override
    public Map<String, Object> getCustomDateInterest(AccountDTO accountDTO) {
        Long accountIdx = accountDTO.getIdx();  // 계좌 Idx
        LocalDateTime endDate = accountDTO.getEndDate();  // 종료일(사용자로부터 입력받은)
        // ㄴ endData 사용자한테 입력받아서 DB에 "2025-03-05T15:45:10.385338200" 이런 형태로 들어가야함

        Account account = accountRepository.findByIdx(accountIdx).orElse(null);
        LocalDateTime maturityDate = account.getMaturityDate();  // 적금만기일
        BigDecimal interestRate = account.getInterestRate().multiply(new BigDecimal("0.01"));;  // 기본금리

        if(endDate.toLocalDate().equals(maturityDate.toLocalDate())) {  // 사용자로부터 입력받은 날짜가 적금만기일이면 getMaturityInterest() -> 만기일이자조회 메서드 실행
            return getMaturityInterest(accountIdx);
        }

        /** 이자 계산(만기일 해지) START */
        // 이자 계산 공통 메서드(idx, 적용금리:기본금리, 해지일:선택일자)
        AccountDTO dto = calculateInterest(accountIdx, interestRate, endDate);
        /** 이자 계산(만기일 해지) END */

        Map<String, Object> result = new HashMap<>();
        result.put("accountDTO", dto);  // 계좌 DTO
        return result;
    }

    /** (4) 계좌 해지 (중도해지이므로 우대금리 X)  =>  만약 해지하는 날짜가 만기일이라면? 만기 해지 화면으로 이동시켜야하나? ? ? ?
     * @param accountDTO idx, accountPassword(계좌 비밀번호)
     * @return 1(성공), 0(실패), 401(비밀번호 불일치)
     */
    // 클라이언트로부터 인증 비밀번호6자리 입력받음. (비밀번호 일치 시에만 로직 수행되도록)
    @Override
    public int terminateAccount(AccountDTO accountDTO) {
        Long idx = accountDTO.getIdx();  // 계좌 idx
        String inputPwd = accountDTO.getAccountPassword();  // 사용자가 입력한 계좌 비밀번호

        Account account = accountRepository.findByIdx(idx).orElse(null);  // 계좌 idx 기준으로 Account 엔티티 조회
        BigDecimal interestRate = account.getInterestRate().multiply(new BigDecimal("0.01"));;  // 기본금리
        LocalDateTime endDate = LocalDateTime.now();  // 종료일 (=해지일)

        // 비밀번호 일치하는지 확인
//      if(!passwordEncoder.matches(inputPwd, account.getAccountPassword())){  // 해당 적금계좌의 현재 비밀번호를 DB에서 조회
        if(!inputPwd.equals(account.getAccountPassword())){  // API테스트용 임시 코드(위에거로 변경해야함)

                // ㄴ 사용자가 입력한 비밀번호가 DB에 저장된 비밀번호와 일치하는지 확인
            return 401;  // 비밀번호 불일치시 401반환
        }
        // 비밀번호가 일치하면 적금계좌 해지 ↓

        /* [적금계좌 해지 프로세스]
        *  1. accountStatus(계좌상태) 컬럼 값 C(해지)로 업데이트
        *  2. interestAmount(세후이자) 컬럼 값 계산 후 업데이트
        *  3. endDate(종료일=해지일) 컬럼 값 업데이트
        * */

        // 2. 세후이자 계산 => 이자계산하는 공통 메서드 사용 calculateInterest(계좌idx, 적용금리(=기본금리), 해지일(=오늘))
        AccountDTO dto = calculateInterest(idx, interestRate, endDate);
        BigDecimal interestAmount = dto.getInterestAmount();  // 세후이자

        account.setAccountStatus('C');  // 1
        account.setInterestAmount(interestAmount);  // 2
        account.setEndDate(endDate);  // 3

        Account updatedAccount = accountRepository.save(account);  // 업데이트 된 account엔티티를 DB에 저장
        // DB 업데이트 되면서 수정일 컬럼도 자동으로 업데이트 됨. 따로 설정 필요없음

        // save() 메서드는 반환 값으로 저장된(업데이트된) 엔티티를 반환함 => 반환된 엔티티가 null이 아니면 업데이트 성공
        if(updatedAccount != null){
            return 1;  // 성공 시 1 반환
        } else {
            return 0;  // 실패 시 0 반환
        }
    }

    /* 메인화면에 필요한 api - 문경미 */
    /**
     * 적금 계좌의 잔액, 총금리 정보
     * @return balanse, final_interest_rate 포함 DTO
     */
    @Override
    public Map<String, Object> getSavingInfo() {

        Map<String, Object> result = new HashMap<>();

        Long memberIdx = TokenContext.getMemberIdx();
        Optional<AccountDTO> accountDTO = entityToDTOWithSaving(accountRepository.findAccountByMember(memberIdx));

        if (accountDTO.isPresent()) {
            AccountDTO dto = accountDTO.get();

            result.put("accountDTO", dto);

            // 만기까지 남은 일수
            LocalDate today = LocalDate.now();
            LocalDate maturityDate = dto.getMaturityDate().toLocalDate();
            long diff = ChronoUnit.DAYS.between(today, maturityDate);

            result.put("diff", diff);
        }

        return result;
    }

    /**
     * 적금 계좌 현재 상태 (단계 및 만기 확인)
     * @return
     */
    @Override
    public Map<String, Object> getAccountCurrent() {

        Map<String, Object> result = new HashMap<>();

        Long memberIdx = TokenContext.getMemberIdx();
        Optional<AccountDTO> accountDTO = entityToDTOWithCurrent(accountRepository.findAccountByMember(memberIdx));

        if (accountDTO.isPresent()) {
            AccountDTO dto = accountDTO.get();

            // 만기일 확인
            LocalDate today = LocalDate.now();
            LocalDate maturityDate = dto.getMaturityDate().toLocalDate();

            if (maturityDate.isEqual(today)) {
                result.put("maturity_yn", "Y");
            } else {
                result.put("maturity_yn", "N");
            }

            // 단계
            int step = 1;
            Long savingCnt = dto.getSavingCnt();

            if (savingCnt >= 21) {
                step = 4;
            } else if (savingCnt < 21 && savingCnt >= 14) {
                step = 3;
            } else if (savingCnt < 14 && savingCnt >= 7) {
                step = 2;
            }
            result.put("account_step", step);
        }

        return result;
    }

    /**
     * 만기 해지 프로세스
     * @param param
     * @return
     */
    @Override
    @Transactional
    public Boolean maturityProcess(Map<String, Object> param) {

        Boolean result  = true;

        try {
            // saving_account update
            Long accountIdx = TokenContext.getSavingAccountIdx();
            BigDecimal interestAmount = new BigDecimal(String.valueOf(param.get("afterTaxInterest")));
            accountRepository.updateMaturity(accountIdx, interestAmount);

            // donation_history insert
            Long memberIdx = TokenContext.getMemberIdx();
            Long organisationIdx = Long.parseLong(String.valueOf(param.get("organisationIdx")));
            BigDecimal interest = new BigDecimal(String.valueOf(param.get("interest")));
            BigDecimal principal = new BigDecimal(String.valueOf(param.get("principal")));
            BigDecimal point = new BigDecimal(String.valueOf(param.get("point")));
            DonationHistory donationHistory = DonationHistory.builder()
                    .member(Member.builder().idx(memberIdx).build())
                    .account(Account.builder().idx(accountIdx).build())
                    .donationOrganization(new DonationOrganization() {{
                        setIdx(organisationIdx);
                     }})
                    .interest(interest)
                    .principal(principal)
                    .point(point)
                    .donationAmount(interest.add(principal).add(point))
                    .donationDate(LocalDateTime.now())
                    .build();
            donationHistoryRepository.save(donationHistory);

        } catch (Exception e) {
            result = false;
        }

        return result;
    }


/** ★★★★★★★★★★★     아래는 공통으로 쓰이는 메서드 분리     ★★★★★★★★★★★ */

    /** [account 엔티티 -> DTO 변환 공통 메서드]
     * account 엔티티 컬럼에 대해 DTO 객체에 기본값을 설정하는 공통 메서드 => 가져다 쓸 때 필요한 값 덮어써서 사용 */
    public AccountDTO entityToDTO(Account account) {
        AccountDTO dto = new AccountDTO();

        // 엔티티의 값을 DTO의 필드에 설정
        dto.setIdx(account.getIdx());  // 계좌 Idx
        dto.setMemberIdx(account.getMember().getIdx());  // 사용자 Idx
        dto.setPaymentAmount(account.getPaymentAmount());  // 납입금액
        dto.setBalance(account.getBalance());  // 잔액
        dto.setInterestRate(account.getInterestRate());  // 기본 금리
        dto.setPrimeRate(account.getPrimeRate());  // 우대 금리
        dto.setFinalInterestRate(account.getFinalInterestRate());  // 최종 금리
        dto.setTaxationYn(account.getTaxationYn());  // 과세 구분
        dto.setDutyRate(account.getDutyRate());  // 세금 비율
        dto.setCreateDate(account.getCreateDate());  // 가입일
        dto.setEndDate(account.getEndDate());  // 종료일(해지일)
        dto.setMaturityDate(account.getMaturityDate());  // 만기일
        dto.setInterestAmount(account.getInterestAmount());  // 세후 이자

        return dto;
    }

    /**
     * 적금 계좌 납입 정보 엔티티 -> DTO
     * @param account
     * @return
     */
    private Optional<AccountDTO> entityToDTOWithSaving(Optional<Account> account) {
        return account.map(entity -> new AccountDTO(
                entity.getBalance(),
                entity.getFinalInterestRate(),
                entity.getMaturityDate()
        ));
    }

    /**
     * 적금 단계, 만기일 엔티티 -> DTO
     * @param account
     * @return
     */
    private Optional<AccountDTO> entityToDTOWithCurrent(Optional<Account> account) {
        return account.map(entity -> new AccountDTO(
                entity.getSavingCnt(),
                entity.getMaturityDate()
        ));
    }

    /** [이자 계산 공통 메서드]
     * param 계좌 idx, 적용금리(최종금리 or 기본금리), 해지일(만기일 or 오늘 or 선택일자)
     * */
    public AccountDTO calculateInterest(Long idx, BigDecimal AppliedInterestRate, LocalDateTime terminationDate) {
        Account account = accountRepository.findByIdx(idx).orElse(null);  // // 적금계좌idx에 대한 적금계좌 엔티티
        List<AccountHistory> accountHistoryList = accountRepository.getAccountHistory(idx);  // 적금계좌idx에 대한 납입내역 엔티티 리스트
        BigDecimal daysInYear = new BigDecimal(365);  // 1년 365일 BigDecimal으로 변환
        BigDecimal totalPreTaxInterestAmount = BigDecimal.ZERO;  // 총 이자금액(세전) = 0;

        /** [이자 계산 공식]
         * 예치일 = 해지일(포함) - 납입일(포함)
         * 이자계산기간 = 예치일/365
         * 총이자(세전) = ∑(납입금액 × 적용금리 × 이자계산기간)
         * */
        for (AccountHistory accountHistory : accountHistoryList) {
            BigDecimal paymentAmount = accountHistory.getPaymentAmount();  // 납입 금액
            LocalDateTime paymentDate = accountHistory.getPaymentDate();  // 납입일
            BigDecimal depositDate = new BigDecimal(ChronoUnit.DAYS.between(paymentDate, terminationDate) + 1);  // 예치일수 = 해지일-납입일
                                     // ChronoUnit.DAYS.between() => 두 날짜 사이의 차이를 일(day) 단위로 계산하여 반환
            BigDecimal interestPeriod = depositDate.divide(daysInYear, 7, BigDecimal.ROUND_HALF_UP);  // 이자계산기간 = 예치일수/365
            BigDecimal preTaxInterestAmount = paymentAmount.multiply(AppliedInterestRate).multiply(interestPeriod);  // 이자금액(세전) = 납입금액*적용금리*이자계산기간
            totalPreTaxInterestAmount = totalPreTaxInterestAmount.add(preTaxInterestAmount);  // 총이자금액(세전) += 이자금액(세전)
        }

        BigDecimal taxAmount = totalPreTaxInterestAmount.multiply(account.getDutyRate());  // 세금액 = 이자금액(세전) × 세금비율(0.154 고정값)
        BigDecimal interestAmount = totalPreTaxInterestAmount.subtract(taxAmount);  // 이자금액(세후) = 이자금액(세전) - 세금액

        AccountDTO dto = entityToDTO(account);  // (엔티티 -> DTO 변환) 공통 메서드 사용
        dto.setEndDate(terminationDate);  // 종료일(=해지일)
        dto.setPreTaxInterestAmount(totalPreTaxInterestAmount.setScale(0, RoundingMode.DOWN));  // 총이자(세전)
        dto.setTaxAmount(taxAmount.setScale(0, RoundingMode.DOWN));  // 세금액
        dto.setInterestAmount(interestAmount.setScale(0, RoundingMode.DOWN));  // 총이자(세후)

        return dto;
    }
}
