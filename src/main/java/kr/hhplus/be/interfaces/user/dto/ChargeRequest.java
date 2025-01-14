package kr.hhplus.be.interfaces.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChargeRequest {
    @NotNull(message = "충전금액은 필수값 입니다.")
    private BigDecimal chargeAmount;
}
