package kr.co.leaf2u_api.card;

import kr.co.leaf2u_api.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
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

    // 자동이체 카드정보 조회 CardDTO 생성자
    public CardDTO(String cardName, String cardNumber, String expirationDate) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardName = cardName;
    }
}
