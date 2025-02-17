package kr.hhplus.be.infrastructure.event;

import kr.hhplus.be.application.order.dto.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentSuccessEvent {
    private final OrderInfo orderInfo;
}
