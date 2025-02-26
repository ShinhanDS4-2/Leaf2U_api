package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountDTO) {

        return ResponseEntity.ok(accountService.createAccount(accountDTO));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity <List<Account>> getAccounts(@PathVariable Long memberId) {

        return ResponseEntity.ok(accountService.getAccountsByMember(memberId));
    }


}
