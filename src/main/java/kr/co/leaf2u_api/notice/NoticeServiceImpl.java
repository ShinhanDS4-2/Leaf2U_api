package kr.co.leaf2u_api.notice;

import jakarta.transaction.Transactional;
import kr.co.leaf2u_api.config.TokenContext;
import kr.co.leaf2u_api.entity.Member;
import kr.co.leaf2u_api.entity.Notice;
import kr.co.leaf2u_api.member.MemberRepository;
import kr.co.leaf2u_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용자 별 알림 리스트
     * @return
     */
    @Override
    public Map<String, Object> getNoticeList() {

        Map<String, Object> result = new HashMap<>();

        Long memberIdx = TokenContext.getMemberIdx();
        List<Notice> todayList = noticeRepository.getNoticeListWithToday(memberIdx);

        List<NoticeDTO> dtoList = todayList.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        result.put("today_list", dtoList);

        List<Notice> prevList = noticeRepository.getNoticeListWithPrev(memberIdx);

        List<NoticeDTO> prevDtoList = prevList.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        result.put("prev_list", prevDtoList);

        return result;
    }

    /**
     * 오늘 알림 등록
     * @return
     */
    @Override
    public Long checkDailyNotice() {

        // 오늘 알림 유무 체크
        Long memberIdx = TokenContext.getMemberIdx();
        Long result = noticeRepository.checkDailyNotice(memberIdx);

        // 오늘 날짜에 등록된 알림이 없을 경우 오늘 알림 등록
        if (result <= 0) {
            Map<String, Object> noticeParam = new HashMap<>();
            noticeParam.put("memberIdx", memberIdx);
            noticeParam.put("title", "오늘의 챌린지");
            noticeParam.put("content", "한달적금, 오늘도 잊지 말고 확인해 보세요.");
            noticeParam.put("category", "D");

            result = registNotice(noticeParam);
        }

        return result;
    }

    /**
     * 알림 등록
     * @param param
     * @return
     */
    @Transactional
    public Long registNotice(Map<String, Object> param) {

        Long memberIdx = TokenContext.getMemberIdx();

        if (memberIdx == null) {
            throw new IllegalArgumentException("memberIdx 가 존재하지 않습니다.");
        }

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        Notice notice = Notice.builder()
                .member(member)
                .title((String) param.get("title"))
                .content((String) param.get("content"))
                .category(((String) param.get("category")).charAt(0))
                .createDate(LocalDateTime.now())
                .build();

        Notice result = noticeRepository.save(notice);

        return result.getIdx();
    }

    /**
     * 알림 엔티티 -> 알림 DTO
     * @param entity
     * @return NoticeDTO
     */
    private NoticeDTO entityToDTO(Notice entity) {

        LocalDateTime createDate = entity.getCreateDate();

        // 현재 날짜와 비교
        LocalDate createLocalDate = createDate.toLocalDate();
        LocalDate today = LocalDate.now();

        String timeAgo = null;
        if (createLocalDate.isEqual(today)) {
            // 오늘 날짜인 경우 몇 시간 전인지 계산
            long hoursAgo = Duration.between(createDate, LocalDateTime.now()).toHours();
            timeAgo = hoursAgo + "시간 전";
        }

        return new NoticeDTO(
                entity.getIdx(),
                entity.getMember().getIdx(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCategory(),
                entity.getCreateDate(),
                CommonUtil.formatDate(entity.getCreateDate(), "MM월 dd일"),
                timeAgo
        );
    }

}
