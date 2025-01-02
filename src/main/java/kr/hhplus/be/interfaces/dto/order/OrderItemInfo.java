package kr.hhplus.be.interfaces.dto.order;

import lombok.Getter;

@Getter
public class OrderItemInfo {
    private Long productId;
    private Integer quantity;
    private Integer price;
}
