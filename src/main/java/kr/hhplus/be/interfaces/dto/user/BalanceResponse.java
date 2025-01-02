package kr.hhplus.be.interfaces.dto.user;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BalanceResponse {

    private long userId;
    private int balance;
}
