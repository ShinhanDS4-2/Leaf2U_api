package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;

import java.math.BigDecimal;

public interface PointService {
    boolean checkIn(Member member);
    void Pedometer(Member member, int points);
    void QuizHint(Member member);
    void QuizCorrect(Member member);
    BigDecimal getTotalPoints(Member member);
    boolean checkTodayActivity(Member member, char activityType);
}
