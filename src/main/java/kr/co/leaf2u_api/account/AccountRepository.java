package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account>findByMemberIdx(Long memberIdx);

    @Query("SELECT a FROM Account a WHERE a.member.idx=:memberIdx AND a.accountStatus='N'")
    Optional<Account> findAccountByMember(Long memberIdx);

}
