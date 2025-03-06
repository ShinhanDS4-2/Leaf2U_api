package kr.co.leaf2u_api.saving;

import java.util.Map;

public interface SavingService {

    // 적금 계좌 납입 내역
    Map<String, Object> getSavingHistoryList(Map<String, Object> param);

    // 챌린지 현황
    Map<String, Object> getChallengeList(Map<String, Object> param);
}
