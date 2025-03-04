package kr.co.leaf2u_api.account;

import kr.co.leaf2u_api.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
                                                // ㄴ HTTP요청의 Body에서 JSON데이터를 가져와 AccountDTO에 매핑
        return ResponseEntity.ok(accountService.createAccount(accountDTO));
        // ??
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

 // 적금 계좌 관리 - 시온
    /** (1) 계좌 기본 정보 조회
     * @param MEMBER(사용자) idx
     * @return Account
     */
    @GetMapping("/info/{memberIdx}")
    public ResponseEntity<Account> getAccountInfo(@PathVariable Long memberIdx) {
        return ResponseEntity.ok(accountService.getAccountInfoById(memberIdx));
    }

    /** (2) 납입금액 변경
     * @param AccountDTO(Idx, accountPassword, paymentAmount)
     * @return
     */
    @PatchMapping("/update/paymentAmount")
    public ResponseEntity<String> updatePaymentAmount(@RequestBody AccountDTO accountDTO) {
        // RequestBody를 사용하면 Spring이 클라이언트로부터 전달받은 JSON 데이터를 자동으로 AccountDTO 객체에 매핑해줌
        // accountDTO 객체의 idx, accountPassword, paymentAmount 필드에 클라이언트에서 전달된 값이 자동으로 저장됨
        int result = accountService.updatePaymentAmount(accountDTO);  // 1 or 0 or 401
        if (result == 1) {
            return ResponseEntity.ok("납입금액이 변경되었습니다. ");
        } else if (result == 401) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("납입금액 변경 실패하였습니다. 다시 시도해주세요.");
        }
    }
// ResponseEntity는 컨트롤러 계층에서 사용하는 것이 좋다 (서비스계층은 비즈니스 로직에 집중하고, HTTP 관련 로직은 컨트롤러에서 처리하도록)


    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    /** (3-1) 예상이자조회 - 만기일해지
     * @param 적금계좌 idx
     * @return Account(적금계좌), InterestRateHistory(금리내역)
     */
    @GetMapping("/interest/maturity")  // 단밀 필드만 수정되는 경우에는 Patch 사용
    public ResponseEntity<Map<String, Object>> getMaturityInterest(@RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.getMaturityInterest(accountDTO));
    }




    // 컨트롤러 다 분리해야함


    /** (4) 계좌 해지
     * @param AccountDTO(Idx->계좌idx, accountPassword->계좌 비밀번호)
     * @return
     */
    @PatchMapping("/termination")
    public ResponseEntity<String> terminateAccount(@RequestBody AccountDTO accountDTO) {
        int result = accountService.terminateAccount(accountDTO);  // 1 or 0 or 401
        if (result == 1) {
            return ResponseEntity.ok("적금 해지가 완료되었습니다. ");
        } else if (result == 401) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("적금 해지가 실패하였습니다. 다시 시도해주세요.");
        }
    }
}
