package kr.co.leaf2u_api.saving;

import kr.co.leaf2u_api.account.AccountRepository;
import kr.co.leaf2u_api.account.AccountService;
import kr.co.leaf2u_api.config.TokenContext;
import kr.co.leaf2u_api.entity.AccountHistory;
import kr.co.leaf2u_api.entity.InterestRateHistory;
import kr.co.leaf2u_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

    private final AccountHistoryRepository accountHistoryRepository;

    private final InterestRateHistoryRepository interestRateRepository;

    private final AccountRepository accountRepository;

    private final AccountService accountService;

    private final SavingRepository savingRepository;

    /**
     * 납입 내역 리스트
     * @return
     */
    @Override
    public Map<String, Object> getSavingHistoryList() {

        Map<String, Object> result = new HashMap<>();

        // 납입내역
        Long accountIdx = TokenContext.getSavingAccountIdx();
        List<AccountHistory> list = accountHistoryRepository.findAccountHistoryListByAccountIdx(accountIdx);

        AtomicInteger rowNum = new AtomicInteger(list.size());

        List<SavingHistoryDTO> dtoList = new ArrayList<>();

        dtoList = list.stream()
                .map(history -> {
                    Long accountHistoryIdx = history.getIdx();
                    List<InterestRateHistory> interestRateList = interestRateRepository.findInterestRateHistoryListByAccountHistoryIdx(accountHistoryIdx);

                    SavingHistoryDTO dto = entityToDTO(history);
                    dto.setInterestRateList(interestRateList);
                    dto.setRowNum((long) rowNum.getAndDecrement());

                    return dto;
                })
                .collect(Collectors.toList());

        result.put("list", dtoList);

        // 계좌 정보
        Map<String, Object> info = accountService.getSavingInfo();
        result.put("info", info);

        return result;
    }

    /**
     * 챌린지 현황
     * @return
     */
    @Override
    public Map<String, Object> getChallengeList() {

        Map<String, Object> result = new HashMap<>();

        // 납입일 리스트
        Long accountIdx = TokenContext.getSavingAccountIdx();
        List<String> paymentDateList = accountHistoryRepository.getFormatPaymentDate(accountIdx);
        result.put("paymentDateList", paymentDateList);

        // 챌린지 별 count
        Map<String, Object> cnt = accountHistoryRepository.getChallengeCnt(accountIdx);
        result.put("challengeCnt", cnt);

        return result;
    }

    /**
     * 납입내역 엔티티 -> 납입내역 DTO
     * @param entity
     * @return SavingHistoryDTO
     */
    private SavingHistoryDTO entityToDTO(AccountHistory entity) {
        return new SavingHistoryDTO(
                entity.getIdx(),
                entity.getMember().getIdx(),
                entity.getAccount().getIdx(),
                entity.getPaymentAmount(),
                entity.getCumulativeAmount(),
                entity.getChallengeType(),
                entity.getPaymentDate(),
                CommonUtil.formatDate(entity.getPaymentDate(), "MM.dd"),
                null,
                null
        );
    }


    @Transactional
    public Map<String, Object> processSavingDeposit(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();

        Long memberIdx = Long.parseLong(String.valueOf(param.get("memberIdx")));
        Long accountIdx = Long.parseLong(String.valueOf(param.get("accountIdx")));
        String challengeType = param.get("challengeType").toString();

        // 🔹 1️⃣ 카드 잔액 차감
        savingRepository.updateCardBalance(accountIdx);

        // 🔹 2️⃣ 적금 납입 내역 추가
        savingRepository.insertSavingHistory(memberIdx, challengeType);

        // 🔹 3️⃣ 매일 금리 (D) 추가
        savingRepository.insertDailyInterest(accountIdx);

        // 🔹 4️⃣ 7번째 납입 시 연속 금리 (W) 추가
        savingRepository.insertWeeklyInterest(accountIdx);

        // 🔹 5️⃣ prime_rate 업데이트
        savingRepository.updatePrimeRate(accountIdx);

        // 🔹 6️⃣ 최종 금리 업데이트
        savingRepository.updateFinalInterestRate(accountIdx);

        // 🔹 7️⃣ 적금 계좌 잔액(balance) 업데이트
        savingRepository.updateSavingAccountBalance(accountIdx);

        // 🔹 8️⃣적금 납입 횟수(saving_cnt) 업데이트
        savingRepository.updateSavingCount(accountIdx);

        result.put("message", "적금 납입이 완료되었습니다.");
        return result;
    }

}
