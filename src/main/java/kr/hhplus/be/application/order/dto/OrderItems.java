package kr.hhplus.be.application.order.dto;

import kr.hhplus.be.domain.order.entity.OrderDetail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItems {
    private List<OrderItemInfo> orderItems;

    public int getTotalAmount() {
        int totalAmount = 0;
        for (OrderItemInfo orderItem : this.orderItems) {
            totalAmount += orderItem.getQuantity() * orderItem.getPrice();
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
