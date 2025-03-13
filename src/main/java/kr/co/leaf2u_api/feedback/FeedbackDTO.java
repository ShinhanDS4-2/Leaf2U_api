package kr.co.leaf2u_api.feedback;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDTO {
    private Long accountIdx;
    private int userChallengeCount; // 사용자의 챌린지 수행 횟수
    private int averageChallengeCount; // 전체 평균 챌린지 수행 횟수
}
