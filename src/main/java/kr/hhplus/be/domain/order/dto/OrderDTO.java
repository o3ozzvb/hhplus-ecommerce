package kr.hhplus.be.domain.order.dto;

import kr.hhplus.be.application.order.dto.OrderItems;

public class OrderDTO {
    private Long orderId;
    private Long userId;
    private Long couponPublishId;
    private int totalAmount;
    private int discountAmount;
    private int finalAmount;
    private OrderItems orderItems;

}
