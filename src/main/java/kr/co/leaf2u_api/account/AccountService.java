package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AccountService {

    Account createAccount(AccountDTO accountDTO);                                  //적금 생성
    List<Account> getAccountsByMember(Long memberId);                              //적금 조회

}
