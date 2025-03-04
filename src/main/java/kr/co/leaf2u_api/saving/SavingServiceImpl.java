package kr.co.leaf2u_api.saving;

import kr.co.leaf2u_api.account.AccountDTO;
import kr.co.leaf2u_api.account.AccountRepository;
import kr.co.leaf2u_api.entity.Account;
import kr.co.leaf2u_api.entity.AccountHistory;
import kr.co.leaf2u_api.entity.InterestRateHistory;
import kr.co.leaf2u_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

    private final AccountHistoryRepository accountHistoryRepository;

    private final InterestRateHistoryRepository interestRateRepository;

    private final AccountRepository accountRepository;

    /**
     * 납입 내역 리스트
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getSavingHistoryList(Map<String, Object> param) {

        Map<String, Object> result = new HashMap<>();

        // 납입내역
        Long accountIdx = Long.parseLong(String.valueOf(param.get("accountIdx")));
        List<AccountHistory> list = accountHistoryRepository.findAccountHistoryListByAccountIdx(accountIdx);

        AtomicInteger rowNum = new AtomicInteger(1);

        List<SavingHistoryDTO> dtoList = new ArrayList<>();

        dtoList = list.stream()
                .map(history -> {
                    Long accountHistoryIdx = history.getIdx();
                    List<InterestRateHistory> interestRateList = interestRateRepository.findInterestRateHistoryListByAccountHistoryIdx(accountHistoryIdx);

                    SavingHistoryDTO dto = entityToDTO(history);
                    dto.setInterestRateList(interestRateList);
                    dto.setRowNum((long) rowNum.getAndIncrement());

                    return dto;
                })
                .collect(Collectors.toList());

        result.put("list", dtoList);

        // 계좌 정보
        Long memberIdx = Long.parseLong(String.valueOf(param.get("memberIdx")));
        Optional<AccountDTO> accountDTO = entityToDTOByAccount(accountRepository.findAccountByMember(memberIdx));
        result.put("info", accountDTO);

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

    private Optional<AccountDTO> entityToDTOByAccount(Optional<Account> account) {
        return account.map(entity -> new AccountDTO(
                entity.getBalance(),
                entity.getFinalInterestRate()
        ));
    }
}
