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
     * @param param (memberIdx, accountIdx)
     * @return
     */
    @PostMapping("/history/list")
    public ResponseEntity<Map<String, Object>> getSavingHistoryList(@RequestBody Map<String, Object> param) {

        Map<String, Object> result = savingService.getSavingHistoryList(param);

        return ResponseEntity.ok(result);
    }

    /**
     * 챌린지 현황
     * @param param (memberIdx, accountIdx)
     * @return
     */
    @PostMapping("/challenge/list")
    public ResponseEntity<Map<String, Object>> getChallengeList(@RequestBody Map<String, Object> param) {

        Map<String, Object> result = savingService.getChallengeList(param);

        return ResponseEntity.ok(result);
    }



}
