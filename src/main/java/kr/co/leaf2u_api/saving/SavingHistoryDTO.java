package kr.co.leaf2u_api.saving;

import kr.co.leaf2u_api.entity.InterestRateHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SavingHistoryDTO {

    private Long idx;
    private Long memberIdx;
    private Long savingAccountIdx;

    private BigDecimal paymentAmount;
    private char challengeType;

    private LocalDateTime paymentDate;
    private String formattedDate;

    // 금리 리스트
    private List<InterestRateHistory> interestRateList;

    // 순번
    private Long rowNum;
}
