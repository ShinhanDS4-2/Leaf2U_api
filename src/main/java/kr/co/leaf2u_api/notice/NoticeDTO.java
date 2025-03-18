package kr.co.leaf2u_api.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {

    private Long idx;
    private Long memberIdx;

    private String title;
    private String content;
    private char category;
    private LocalDateTime crateDate;
    private String formatedDate;

    // n 시간 전
    private String timeAgo;
}
