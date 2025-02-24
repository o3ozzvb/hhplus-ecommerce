package kr.hhplus.be.application.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItems implements Serializable {
    private List<OrderItemInfo> orderItems;

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemInfo orderItem : this.orderItems) {
            BigDecimal itemTotalPrice = orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotalPrice);
        }
        return totalAmount;
    }

    public List<OrderDetail> toOrderDetails() {
        List<OrderDetail> orderDetails = new ArrayList<>();

        for(OrderItemInfo orderItem : this.orderItems) {
            orderDetails.add(orderItem.toOrderDetail());
        }

        return orderDetails;
    }

    public static OrderItems fromOrderDetails(List<OrderDetail> orderDetails) {
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetails) {
            orderItemInfos.add(OrderItemInfo.from(orderDetail));
        }

        return new OrderItems(orderItemInfos);
    }
}
