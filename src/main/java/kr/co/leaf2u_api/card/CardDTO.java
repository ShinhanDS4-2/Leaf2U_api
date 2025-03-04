package kr.co.leaf2u_api.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
