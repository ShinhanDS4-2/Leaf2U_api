package kr.co.leaf2u_api.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {

    private Long memberIdx;
    private Long savingAccountIdx;
    private char cardType;
    private String cardName;  // 카드명
    private String cardNumber;  // 카드번호
    private String expirationDate;  // 유효기간
    private String accountNumber;
    private BigDecimal balance;
    private String cardPassword;


}
