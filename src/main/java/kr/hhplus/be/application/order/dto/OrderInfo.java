package kr.hhplus.be.application.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo implements Serializable {

    private Long orderId;

    private Long userId;

    private Long couponPublishId;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDateTime;

    private OrderItems orderItems;

    public static OrderInfo from(Order order, List<OrderDetail> orderDetails) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.orderId = order.getId();
        orderInfo.userId = order.getRefUserId();
        orderInfo.couponPublishId = order.getRefCouponPublishId();
        orderInfo.totalAmount = order.getTotalAmount();
        orderInfo.discountAmount = order.getDiscountAmount();
        orderInfo.finalAmount = order.getFinalAmount();
        orderInfo.orderDateTime = order.getOrderedAt();
        orderInfo.orderItems = OrderItems.fromOrderDetails(orderDetails);

        return orderInfo;
    }
}
