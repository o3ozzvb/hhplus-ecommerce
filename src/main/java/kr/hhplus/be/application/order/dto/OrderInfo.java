package kr.hhplus.be.application.order.dto;

import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo {
    private Long orderId;
    private Long userId;
    private Long couponPublishId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime orderDateTime;
    private OrderItems orderItems;

    public static OrderInfo from(Order order) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.orderId = order.getId();
        orderInfo.userId = order.getRefUserId();
        orderInfo.couponPublishId = order.getRefCouponPublishId();
        orderInfo.totalAmount = order.getTotalAmount();
        orderInfo.discountAmount = order.getDiscountAmount();
        orderInfo.finalAmount = order.getFinalAmount();
        orderInfo.orderDateTime = order.getOrderedAt();
        orderInfo.orderItems = OrderItems.fromOrderDetails(order.getOrderDetails());

        return orderInfo;
    }
}
