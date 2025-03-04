package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import kr.co.leaf2u_api.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicService {

    @Value("${newsapi.api.key}")
    private String NEWS_API_KEY;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OpenAIService openaiService;

    private final TopicRepository topicRepository;

    public List<EcoTips> getEcoTips(char category) {
        return topicRepository.findByCategory(category);
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
}