package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;

import java.util.List;
import java.util.Map;

public interface TopicService {

    List<EcoTips> getEcoTips();
    EcoTips saveEcoTips(EcoTips  ecoTips);

    List<Map<String, Object>> getNews();
    String createQuiz(String title, String content);

    Map<String, Object> getFineDustInfo();
}
