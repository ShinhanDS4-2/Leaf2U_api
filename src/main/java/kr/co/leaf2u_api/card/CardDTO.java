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
    private String cardName;
    private String cardNumber;
    private String expirationDate;
    private String accountNumber;
    private BigDecimal balance;
    private String cardPassword;

    public CardDTO(String cardName, String accountNumber) {
        this.cardName = cardName;
        this.accountNumber = accountNumber;
    }
}
