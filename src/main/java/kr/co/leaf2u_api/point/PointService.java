package kr.co.leaf2u_api.point;

import kr.co.leaf2u_api.entity.Member;

public interface PointService {
    boolean checkIn(Member member);
    void addPedometerPoints(Member member, int points);

    void addQuizHintPoint(Member member);
    boolean checkQuizAnswer(String answer);
    void addQuizCorrectPoint(Member member);
}
