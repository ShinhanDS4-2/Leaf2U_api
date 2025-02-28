package kr.co.leaf2u_api.card;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CardDTO {

    private Long memberIdx;
    private Long savingAccountIdx;
    private char cardType;
    private String cardName;
    private String cardNumber;
    private String expirationDate;
    private String accountNumber;
    private BigDecimal balance;
}
