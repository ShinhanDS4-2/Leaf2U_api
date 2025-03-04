package kr.co.leaf2u_api.saving;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saving")
@RequiredArgsConstructor
public class SavingController {

    private final SavingService savingService;

    /**
     * 납입내역 리스트
     * @param param
     * @return
     */
    @PostMapping("/history/list")
    public ResponseEntity<Map<String, Object>> getSavingHistoryList(@RequestBody Map<String, Object> param) {

        Map<String, Object> result = savingService.getSavingHistoryList(param);

        return ResponseEntity.ok(result);
    }


}
