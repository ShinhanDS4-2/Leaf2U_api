package kr.co.leaf2u_api.saving;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/saving")
@RequiredArgsConstructor
public class SavingController {

    private final SavingService savingService;

    /**
     * 납입내역 리스트
     * @return
     */
    @PostMapping("/history/list")
    public ResponseEntity<Map<String, Object>> getSavingHistoryList() {

        Map<String, Object> result = savingService.getSavingHistoryList();

        return ResponseEntity.ok(result);
    }

    /**
     * 챌린지 현황
     * @return
     */
    @PostMapping("/challenge/list")
    public ResponseEntity<Map<String, Object>> getChallengeList() {

        Map<String, Object> result = savingService.getChallengeList();

        return ResponseEntity.ok(result);
    }

    /**
     * 비밀번호 검증 API
     */
    @PostMapping("/password")
    public ResponseEntity<Boolean> verifyPassword(@RequestBody Map<String, String> request) {
        String inputPassword = request.get("inputPassword");
        boolean isMatch = savingService.verifyPassword(inputPassword);
        return ResponseEntity.ok(isMatch);
    }

    /**
     * 적금 prime_rate
     */
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> processSavingDeposit(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = savingService.processSavingDeposit(param);
        return ResponseEntity.ok(result);
    }

}
