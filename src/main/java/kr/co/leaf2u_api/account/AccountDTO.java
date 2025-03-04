package kr.co.leaf2u_api.account;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountDTO {

    private Long memberIdx;
    private String accountPassword;
    private Boolean cardYn;

    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal interestRate;
    private BigDecimal primeRate;

}
