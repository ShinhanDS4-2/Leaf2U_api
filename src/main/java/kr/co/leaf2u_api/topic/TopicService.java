package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;

import java.util.List;

public interface TopicService {

    List<EcoTips> getEcoTips(char category);
    EcoTips saveEcoTips(EcoTips  ecoTips);
}
