package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import kr.co.leaf2u_api.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    /**
     * 뉴스 리스트
     * @return
     */
//    public List<Map<String, Object>> getNews() {
//        String url = "https://newsapi.org/v2/everything?q=기후&language=ko&sortBy=sim&apiKey=" + NEWS_API_KEY;
//
//        // 뉴스 API 호출
//        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
//
//        List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");
//
//        List<Map<String, Object>> resultList = new ArrayList<>();
//
//        for (Map<String, Object> article : articles) {
//            // 날짜를 "YYYY-MM-DD" 형식으로 반환
//            String publishedAt = (String) article.get("publishedAt");
//            String formattedDate = (publishedAt != null && publishedAt.length() >= 10) ? publishedAt.substring(0, 10) : "N/A";
//
//            resultList.add(Map.of(
//                    "title", (String) article.get("title"),
//                    "description", (String) article.get("description"),
//                    "url", (String) article.get("url"),
//                    "date", formattedDate
//            ));
//        }
//
//
//
////        // 첫 번째 기사만 선택
////        Map<String, Object> article = articles.get(0);
//
//        return resultList;
//    }


    public List<Map<String, Object>> getNews() {
        String url = "https://newsapi.org/v2/everything?q=기후&language=ko&sortBy=sim&apiKey=" + NEWS_API_KEY;

        // 뉴스 API 호출
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");

        List<Map<String, Object>> filteredList = new ArrayList<>();
        List<String> ecoKeywords = List.of("환경", "기후", "오염", "탄소", "에너지", "재생", "지구", "미세먼지", "온실가스", "전기차");

        // 환경 관련 기사 10개 추출
        for (Map<String, Object> article : articles) {
            String title = (String) article.get("title");
            String description = (String) article.get("description");

            boolean isEcoRelated = ecoKeywords.stream().anyMatch(keyword ->
                    (title != null && title.contains(keyword)) || (description != null && description.contains(keyword))
            );

            if (isEcoRelated) {
                String publishedAt = (String) article.get("publishedAt");
                String formattedDate = (publishedAt != null && publishedAt.length() >= 10) ? publishedAt.substring(0, 10) : "N/A";

                filteredList.add(Map.of(
                        "title", title,
                        "description", description,
                        "url", article.get("url"),
                        "date", formattedDate
                ));
            }

            if (filteredList.size() >= 10) {
                break;
            }
        }

        // 10개 기사 중에서 랜덤하게 3개 선택
        Collections.shuffle(filteredList, new Random());
        return filteredList.size() > 3 ? filteredList.subList(0, 3) : filteredList;
    }


    /**
     * OpenAIService를 활용하여 퀴즈 생성
     * @param title
     * @param content
     * @return
     */
    public String createQuiz(String title, String content) {
        return openaiService.createQuiz(title, content);
    }

    /**
     * 미세먼지api 가져오기
     * @return
     */
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
