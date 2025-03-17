package kr.co.leaf2u_api.saving;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.account.AccountRepository;
import kr.co.leaf2u_api.entity.*;
import kr.co.leaf2u_api.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest
public class SavingRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHistoryRepository accountHistoryRepository;

    @Autowired
    private InterestRateHistoryRepository interestRateRepository;

    // 납입내역, 금리내역 insert
    @Test
    @Transactional
    @Commit
    public void insertSavingHistory() {

        IntStream.rangeClosed(1,12).forEach(i -> {

            Optional<Member> memberOptional = memberRepository.findById(23L);
            Optional<Account> accountOptional = accountRepository.findByMemberIdx(23L).stream().findFirst();

            Member member = memberOptional.get();
            Account account = accountOptional.get();

            AccountHistory history = AccountHistory.builder()
                    .member(member)
                    .account(account)
                    .challengeType('T')
                    .paymentAmount(BigDecimal.valueOf(30000))
                    .paymentDate(LocalDateTime.now())
                    .build();

            AccountHistory savedHistory =  accountHistoryRepository.save(history);

            InterestRateHistory interestRateHistory = InterestRateHistory.builder()
                    .account(account)
                    .accountHistory(savedHistory)
                    .rateType('D')
                    .rate(BigDecimal.valueOf(0.10))
                    .createDate(LocalDateTime.now())
                    .build();

            interestRateRepository.save(interestRateHistory);

            if (i % 7 == 0) {
                InterestRateHistory weekInterestRateHistory = InterestRateHistory.builder()
                        .account(account)
                        .accountHistory(savedHistory)
                        .rateType('W')
                        .rate(BigDecimal.valueOf(0.20))
                        .createDate(LocalDateTime.now())
                        .build();

                interestRateRepository.save(weekInterestRateHistory);
            }
        });
    }

    @Test
    public void getSavingHistoryList() {

        List<AccountHistory> list = accountHistoryRepository.findAccountHistoryListByAccountIdx(2L);

        for (AccountHistory accountHistory : list) {
            System.out.println(accountHistory);
        }
    }

    @Autowired
    private SavingService savingService;

    @Test
    @Commit
    public void testSavingInsert() {
        Random random = new Random();
        List<String> challengeTypes = Arrays.asList("T", "T", "T", "T", "R", "C", "R");

        // 28
        for (int i = 18; i < 19; i++) {
            Long num = Long.valueOf(i);
            Optional<Account> account = accountRepository.findByIdx(num);
            Account acc = account.get();

            // 날짜
            LocalDateTime start = acc.getCreateDate();
            LocalDateTime end = acc.getMaturityDate();
            long days = ChronoUnit.DAYS.between(start, end);

            Map<String, Object> param = new HashMap<>();
            param.put("memberIdx", acc.getMember().getIdx());
            param.put("accountIdx", acc.getIdx());

            for (int j = 0; j < days; ) {
                param.put("challengeType", challengeTypes.get(random.nextInt(challengeTypes.size())));
                savingService.testProcessDeposit(param);

                LocalDateTime currentDate = start.plusDays(j);
                System.out.println(currentDate);

                j += random.nextBoolean() ? 1 : 2; // 50% 확률로 1 또는 2 증가
            }


        }
    }
}
