package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import kr.co.leaf2u_api.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Value("${newsapi.api.key}")
    private String NEWS_API_KEY;

    @Value("${finedust.api.key}")
    private String FINE_DUST_API_KEY;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OpenAIService openaiService;

    public List<EcoTips> getEcoTips(char category) {
        return  topicRepository.findByCategory(category);
    }

    public EcoTips saveEcoTips(EcoTips ecoTips) {
        return topicRepository.save(ecoTips);
    }

    public Map<String, Object> getNews(String keyword) {
        String url = "https://newsapi.org/v2/everything?q=" + keyword + "+&language=ko&sortBy=relevancy&apiKey=" + NEWS_API_KEY;

        // 뉴스 API 호출
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");

        if (articles == null || articles.isEmpty()) {
            return Map.of("error", "No articles found");
        }

        // 첫 번째 기사만 선택
        Map<String, Object> article = articles.get(0);

        // 날짜를 "YYYY-MM-DD" 형식으로 반환
        String publishedAt = (String) article.get("publishedAt");
        String formattedDate = (publishedAt != null && publishedAt.length() >= 10) ? publishedAt.substring(0, 10) : "N/A";


        return Map.of(
                "title", (String) article.get("title"),
                "description", (String) article.get("description"),
                "url", (String) article.get("url"),
                "date", formattedDate
        );
    }

    // OpenAIService를 활용하여 퀴즈 생성
    public String createQuiz(String title, String content) {
        return openaiService.createQuiz(title, content);
    }
    //미세먼지api 가져오기
    public Map<String, Object> getFineDustInfo(String location) {
        String url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"
                + "?stationName=" + location
                + "&dataTerm=daily"
                + "&ver=1.3"
                + "&serviceKey=" + FINE_DUST_API_KEY
                + "&returnType=json";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) response.get("response")).get("body");

        if (items == null || items.isEmpty()) {
            return Map.of("error", "No data found for location: " + location);
        }

        Map<String, Object> latestData = items.get(0);
        int pm10 = Integer.parseInt((String) latestData.get("pm10Value"));
        int pm25 = Integer.parseInt((String) latestData.get("pm25Value"));

        String pm10Status = getFineDustStatus(pm10, true);
        String pm25Status = getFineDustStatus(pm25, false);

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return Map.of(
                "location", location,
                "timestamp", currentTime,
                "pm10", pm10,
                "pm10Status", pm10Status,
                "pm25", pm25,
                "pm25Status", pm25Status
        );
    }

    private String getFineDustStatus(int value, boolean isPm10) {
        if (isPm10) {
            return (value <= 80) ? "좋음" : (value <= 150) ? "보통" : "나쁨";
        } else {
            return (value <= 35) ? "좋음" : (value <= 75) ? "보통" : "나쁨";
        }
    }
}
