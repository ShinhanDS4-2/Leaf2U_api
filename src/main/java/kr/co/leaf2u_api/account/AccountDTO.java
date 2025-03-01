package kr.co.leaf2u_api.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {

    private Long memberIdx;
    private String accountPassword;
    private Boolean cardYn;



}
