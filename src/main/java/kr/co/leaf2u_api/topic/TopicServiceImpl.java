package kr.co.leaf2u_api.topic;

import kr.co.leaf2u_api.entity.EcoTips;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    public List<EcoTips> getEcoTips(char category) {
        return  topicRepository.findByCategory(category);
    }

    public EcoTips saveEcoTips(EcoTips ecoTips) {
        return topicRepository.save(ecoTips);
    }
}
