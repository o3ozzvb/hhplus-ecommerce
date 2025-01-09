package kr.hhplus.be.application.payment.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCommand {
    private Long userId;
    private Long orderId;
    private Integer payAmount;
}
