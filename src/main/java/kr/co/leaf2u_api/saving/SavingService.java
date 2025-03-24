package kr.co.leaf2u_api.saving;

import java.util.Map;

public interface SavingService {

    // 적금 계좌 납입 내역
    Map<String, Object> getSavingHistoryList();

    // 챌린지 현황
    Map<String, Object> getChallengeList();

    // 비밀번호 검증 API
    boolean verifyPassword(String inputPassword);

    // 적금 납입 & 우대 금리
    Map<String, Object> processSavingDeposit(Map<String, Object> param);
}
