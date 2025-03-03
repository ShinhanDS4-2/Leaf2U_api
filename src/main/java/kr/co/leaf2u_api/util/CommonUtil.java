package kr.co.leaf2u_api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtil {

    /**
     * LocalDateTime 포맷 변환
     * @param date
     * @param format
     * @return format에 맞게 변환된 날짜 문자열
     */
    public static String formatDate(LocalDateTime date, String format) {

        if (date == null || format == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return date.format(formatter);
    }
}
