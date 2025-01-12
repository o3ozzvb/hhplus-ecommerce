package kr.hhplus.be.interfaces.order.dto;

import kr.hhplus.be.application.order.dto.OrderItemInfo;
import lombok.Getter;

@Getter
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
    private Integer price;

    public OrderItemInfo toOrderItemInfo() {
        return new OrderItemInfo(productId, quantity, price);
    }
}
