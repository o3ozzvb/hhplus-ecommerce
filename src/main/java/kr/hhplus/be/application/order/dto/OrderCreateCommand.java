package kr.hhplus.be.application.order.dto;

import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderCreateCommand {
    private Long userId;
    private Long couponPublishId;
    private OrderItems orderItems;

    public Order toOrder(int totalAmount, int discountAmount) {
        return Order.builder()
                .refCouponPublishId(this.couponPublishId)
                .refUserId(this.userId)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .finalAmount(totalAmount - discountAmount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
