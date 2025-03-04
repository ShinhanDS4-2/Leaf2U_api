package kr.co.leaf2u_api.saving;

import java.util.List;
import java.util.Map;

public interface SavingService {

    // 적금 계좌 납입 내역
    Map<String, Object> getSavingHistoryList(Map<String, Object> param);
}
