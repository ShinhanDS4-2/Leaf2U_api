package kr.co.leaf2u_api.challenge;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 업로드
 * */
@Getter
@Setter
public class ChallengeRequestDto {
    private MultipartFile image;
}
