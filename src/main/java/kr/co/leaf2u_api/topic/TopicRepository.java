package kr.co.leaf2u_api.topic;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import kr.co.leaf2u_api.entity.EcoTips;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<EcoTips, Long> {

    @Query("SELECT e FROM EcoTips e ORDER BY FUNCTION('RAND') LIMIT 2")
    List<EcoTips> findRandomTips();
}
