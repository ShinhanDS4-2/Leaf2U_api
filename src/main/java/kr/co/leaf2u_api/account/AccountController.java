package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 적금 가입
     * 소셜 로그인한 사용자 정보를 accountDTO로 넘겨줌
     * @param accountDTO
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountDTO) {

        return ResponseEntity.ok(accountService.createAccount(accountDTO));
    }

    /**
     * 가입한 적금 조회
     * @param memberId
     * @return
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity <List<Account>> getAccounts(@PathVariable Long memberId) {

        return ResponseEntity.ok(accountService.getAccountsByMember(memberId));
    }


}
