package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountDTO accountDTO);
    List<Account> getAccountsByMember(Long memberId);
}
