package kr.co.leaf2u_api.topic;

import org.springframework.stereotype.Repository;
import kr.co.leaf2u_api.entity.EcoTips;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<EcoTips, Long> {
    List<EcoTips> findByCategory(char category);
}
