package kr.co.leaf2u_api.ai.challenge;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ChallengeService {
    ChallengeResponseDto analyzeImage(MultipartFile image);
}
