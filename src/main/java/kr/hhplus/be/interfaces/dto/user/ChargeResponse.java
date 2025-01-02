package kr.hhplus.be.interfaces.dto.user;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChargeResponse {
    private int balance;
}
