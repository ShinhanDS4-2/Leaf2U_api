package kr.co.leaf2u_api.saving;

import java.util.List;
import java.util.Map;

public interface SavingService {

    List<SavingHistoryDTO> getSavingHistoryList(Map<String, Object> param);
}
