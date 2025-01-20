package kr.hhplus.be.interfaces.payment.dto;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.application.payment.dto.PaymentCommand;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PaymentRequest {
    @NotNull(message = "사용자 ID는 필수값 입니다.")
    private Long userId;

    @NotNull(message = "주문 ID는 필수값 입니다.")
    private Long orderId;

    @NotNull(message = "결제금액은 필수값 입니다.")
    private BigDecimal payAmount;

    public PaymentCommand toPaymentCommand() {
        return new PaymentCommand(this.userId, this.orderId, this.payAmount);
    }
}
