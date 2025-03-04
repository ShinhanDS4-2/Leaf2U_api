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
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {

        AccountDTO createdAccount=accountService.createAccount(accountDTO);
        return ResponseEntity.ok(createdAccount);
    }

    /**
     * 가입한 적금 조회
     * @param memberId
     * @return
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity <List<AccountDTO>> getAccounts(@PathVariable Long memberId) {

        List<AccountDTO> accounts=accountService.getAccountsByMember(memberId);
        return ResponseEntity.ok(accounts);
    }


}