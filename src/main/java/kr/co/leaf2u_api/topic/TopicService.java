package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import kr.co.leaf2u_api.topic.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<EcoTips> getEcoTips(char category) {
        return  topicRepository.findByCategory(category);
    }

    public EcoTips saveEcoTips(EcoTips  ecoTips) {
        return topicRepository.save(ecoTips);}
}
