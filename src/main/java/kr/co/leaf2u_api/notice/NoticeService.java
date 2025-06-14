package kr.co.leaf2u_api.notice;

import java.util.Map;

public interface NoticeService {

    // 사용자 별 알림 리스트
    Map<String, Object> getNoticeList();

    // 오늘 알림 등록
    Long checkDailyNotice();

    // 알림 등록
    Long registNotice(Map<String, Object> param);
}
