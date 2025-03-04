package kr.co.leaf2u_api.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 사용자 별 알림 리스트
     * @param param (memberIdx)
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> getNoticeList(@RequestBody Map<String, Object> param) {

        Map<String, Object> result = noticeService.getNoticeList(param);

        return ResponseEntity.ok(result);
    }

    /**
     * 오늘 알림 등록
     * @param param (memberIdx)
     * @return
     */
    @PostMapping("/daily")
    public ResponseEntity<Long> checkDailyNotice(@RequestBody Map<String, Object> param) {

        Long noticeIdx = noticeService.checkDailyNotice(param);

        return ResponseEntity.ok(noticeIdx);
    }
}
