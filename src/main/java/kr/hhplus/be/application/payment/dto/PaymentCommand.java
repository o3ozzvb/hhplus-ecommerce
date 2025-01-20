package kr.hhplus.be.application.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCommand {
    private Long userId;
    private Long orderId;
    private BigDecimal payAmount;
}
