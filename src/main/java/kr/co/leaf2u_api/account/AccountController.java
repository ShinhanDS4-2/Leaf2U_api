package kr.co.leaf2u_api.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
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


    /* 적금 계좌 관리 API - 시온 */
    /** (1) 계좌 기본정보 조회
     * @return AccountDTO
     */
    @GetMapping("/info/")
    public ResponseEntity<Map<String, Object>> getAccountInfo() {
        return ResponseEntity.ok(accountService.getAccountInfoById());
    }

    /** (2) 납입금액 변경
     * @param accountDTO
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

    /** (3-1) 예상이자조회 - 만기일해지
     * @return account(적금계좌), interestRateHistory(금리내역)
     */
    @GetMapping("/interest/maturity")
    public ResponseEntity<Map<String, Object>> getMaturityInterest() throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.getMaturityInterest());
    }

    /** (3-2) 예상이자조회 - 오늘해지
     * @return Account(적금계좌)
     */
    @GetMapping("/interest/today")  // 단밀 필드만 수정되는 경우에는 Patch 사용
    public ResponseEntity<Map<String, Object>> getTodayInterest() throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.getTodayInterest());
    }

    /** (3-3) 예상이자조회 - 선택일자 해지
     * @param endDate(이자 계산기간 종료일 - 선택일자 해지에 필요)
     * @return Account(적금계좌)
     */
    @GetMapping("/interest/customDate")  // 단밀 필드만 수정되는 경우에는 Patch 사용
    public ResponseEntity<Map<String, Object>> getCustomDateInterest(@RequestParam("endDate") LocalDateTime endDate) throws AccountNotFoundException {
        System.out.println("받은 종료일: " + endDate);
        return ResponseEntity.ok(accountService.getCustomDateInterest(endDate));
    }

    /** (4) 계좌 해지 (중도해지이므로 우대금리 X)
     * @param accountDTO idx, accountPassword(계좌 비밀번호)
     * @return 1(성공), 0(실패), 401(비밀번호 불일치)
     */
    @PatchMapping("/termination")
    public ResponseEntity<String> terminateAccount(@RequestBody AccountDTO accountDTO) throws AccountNotFoundException {
        int result = accountService.terminateAccount(accountDTO);  // 1 or 0 or 401
        if (result == 1) {
            return ResponseEntity.ok("적금 해지가 완료되었습니다. ");
        } else if (result == 401) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("적금 해지가 실패하였습니다. 다시 시도해주세요.");
        }
    }

    /* 메인화면에 필요한 api - 문경미 */
    /**
     * 메인홈 적금 계좌 정보
     * @return 단계, 오늘 납입 유무, 납입금액, 만기 확인, 총금리, 총잔액
     */
    @PostMapping("/saving/info")
    public ResponseEntity<Map<String, Object>> getSavingInfo() {

        Map<String, Object> result = accountService.getSavingInfo();

        return ResponseEntity.ok(result);
    }

    /* 만기 해지 api - 문경미 */

    /**
     * 만기 해지 프로세스
     * @param param (organizationIdx, afterTaxInterest(세후이자), interest(이자 후원금), principal(원금 후원금), point(포인트 후원금)
     * @return
     */
    @PostMapping("/maturity")
    public ResponseEntity<Boolean> maturityProcess(@RequestBody Map<String, Object> param) {

        Boolean result = accountService.maturityProcess(param);

        return ResponseEntity.ok(result);
    }

}