package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;

import java.util.List;
import java.util.Map;
//
public interface TopicService {

    List<EcoTips> getEcoTips(char category);
    EcoTips saveEcoTips(EcoTips  ecoTips);

    Map<String, Object> getNews(String keyword);
    String createQuiz(String title, String content);
}
