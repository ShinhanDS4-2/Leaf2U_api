package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account>findByMemberIdx(Long memberIdx);


}
