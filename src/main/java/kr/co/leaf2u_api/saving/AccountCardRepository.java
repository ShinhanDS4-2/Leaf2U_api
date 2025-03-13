package kr.co.leaf2u_api.saving;


import kr.co.leaf2u_api.entity.AccountCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCardRepository extends JpaRepository<AccountCard, Long> {
}
