package kr.hhplus.be.interfaces.order.dto;

import kr.hhplus.be.application.order.dto.OrderInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderResponse {
    private long orderId;
    private int totalAmount;
    private LocalDateTime orderDateTime;

    public static OrderResponse from(OrderInfo orderInfo) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.orderId = orderInfo.getOrderId();
        orderResponse.totalAmount = orderInfo.getTotalAmount();
        orderResponse.orderDateTime = orderInfo.getOrderDateTime();

        return orderResponse;
    }
}
