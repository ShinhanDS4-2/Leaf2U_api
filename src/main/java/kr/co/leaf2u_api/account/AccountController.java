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

    /**
     * 적금 가입
     * 소셜 로그인한 사용자 정보를 accountDTO로 넘겨줌
     * @param accountDTO
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountDTO) {
                                                // ㄴ HTTP요청의 Body에서 JSON데이터를 가져와 AccountDTO에 매핑
        return ResponseEntity.ok(accountService.createAccount(accountDTO));
        // ??
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity <List<Account>> getAccounts(@PathVariable Long memberId) {

        return ResponseEntity.ok(accountService.getAccountsByMember(memberId));
    }


}
