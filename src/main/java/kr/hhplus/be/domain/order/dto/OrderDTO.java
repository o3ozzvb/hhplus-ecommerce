package kr.hhplus.be.domain.order.dto;

import kr.hhplus.be.application.order.dto.OrderItems;

import java.math.BigDecimal;

public class OrderDTO {
    private Long orderId;
    private Long userId;
    private Long couponPublishId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private OrderItems orderItems;

}
