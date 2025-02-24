package kr.hhplus.be.application.order.dto;

import kr.hhplus.be.domain.order.entity.OrderDetail;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemInfo implements Serializable {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    public OrderDetail toOrderDetail() {
        int quantity = this.getQuantity();
        BigDecimal price = this.getPrice();

        return OrderDetail.builder()
                .id(null)
                .refOrderId(null)
                .refProductId(productId)
                .quantity(quantity)
                .price(price)
                .totalAmount(price.multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    public static OrderItemInfo from(OrderDetail orderDetail) {
        OrderItemInfo orderItemInfo = new OrderItemInfo();

        orderItemInfo.productId = orderDetail.getRefProductId();
        orderItemInfo.quantity = orderDetail.getQuantity();
        orderItemInfo.price = orderDetail.getPrice();

        return orderItemInfo;
    }
}
