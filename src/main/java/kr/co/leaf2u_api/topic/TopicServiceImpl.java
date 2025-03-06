package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import kr.co.leaf2u_api.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;

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
    public Map<String, Object> getFineDustInfo() {
        String apiUrl = "http://openAPI.seoul.go.kr:8088/" + FINE_DUST_API_KEY + "/xml/ListAvgOfSeoulAirQualityService/1/5/";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            String xmlData = response.getBody();

            // XML을 파싱하여 필요한 데이터 추출
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlData)));

            NodeList items = document.getElementsByTagName("row");
            if (items.getLength() == 0) {
                return Map.of("error", "No data available");
            }

            Element element = (Element) items.item(0);
            int pm10 = Integer.parseInt(getTagValue("PM10", element));
            int pm25 = Integer.parseInt(getTagValue("PM25", element));
            // 현재 날짜 및 시간 가져오기
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            return Map.of(
                    "pm10", pm10,
                    "pm10Status", getPm10Status(pm10),
                    "pm25", pm25,
                    "pm25Status", getPm25Status(pm25),
                    "dateTime", formattedDateTime  // 현재 날짜 및 시간 추가
            );
        } catch (Exception e) {
            return Map.of("error", "Failed to fetch data", "details", e.getMessage());
        }
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        return nodeList.getLength() > 0 ? nodeList.item(0).getTextContent() : "0";
    }

    private String getPm10Status(int value) {
        if (value <= 80) return "좋음";
        if (value <= 150) return "보통";
        return "나쁨";
    }

    private String getPm25Status(int value) {
        if (value <= 35) return "좋음";
        if (value <= 75) return "보통";
        return "나쁨";
    }

}
