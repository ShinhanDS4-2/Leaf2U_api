package kr.co.leaf2u_api.challenge;

import org.springframework.stereotype.Service;

@Service
public interface ChallengeService {
    ChallengeResponseDto analyzeImage(ChallengeRequestDto requestDto);
}
