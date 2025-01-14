package kr.hhplus.be.interfaces.user.dto;

import kr.hhplus.be.domain.user.dto.BalanceDTO;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BalanceResponse {

    private long userId;
    private BigDecimal balance;

    public static BalanceResponse from(BalanceDTO balanceDTO) {
        BalanceResponse response = new BalanceResponse();

        response.userId = balanceDTO.getUserId();
        response.balance = balanceDTO.getBalance();

        return response;
    }
}
