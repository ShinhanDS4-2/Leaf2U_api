package kr.co.leaf2u_api.saving;

import kr.co.leaf2u_api.entity.AccountHistory;
import kr.co.leaf2u_api.entity.InterestRateHistory;
import kr.co.leaf2u_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

    private final AccountHistoryRepository accountHistoryRepository;

    private final InterestRateHistoryRepository interestRateRepository;

    @Override
    public List<SavingHistoryDTO> getSavingHistoryList(Map<String, Object> param) {

        // 납입내역
        Long accountIdx = Long.parseLong(String.valueOf(param.get("accountIdx")));
        List<AccountHistory> list = accountHistoryRepository.findAccountHistoryListByAccountIdx(accountIdx);

        AtomicInteger rowNum = new AtomicInteger(1);

        return list.stream()
                .map(history -> {
                    Long accountHistoryIdx = history.getIdx();
                    List<InterestRateHistory> interestRateList = interestRateRepository.findInterestRateHistoryListByAccountHistoryIdx(accountHistoryIdx);

                    SavingHistoryDTO dto = entityToDTO(history);
                    dto.setInterestRateList(interestRateList);
                    dto.setRowNum((long) rowNum.getAndIncrement());

                    return dto;
                })
                .collect(Collectors.toList());
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
                entity.getChallengeType(),
                entity.getPaymentDate(),
                CommonUtil.formatDate(entity.getPaymentDate(), "MM.dd"),
                null,
                null
        );
    }
}
