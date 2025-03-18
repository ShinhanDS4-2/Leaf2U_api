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
import java.util.List;
import java.util.Optional;
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
}
