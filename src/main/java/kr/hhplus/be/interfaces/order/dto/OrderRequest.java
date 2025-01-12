package kr.hhplus.be.interfaces.order.dto;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class OrderRequest {
    @NotNull(message = "사용자 ID는 필수값 입니다.")
    private Long userId;

    @NotNull(message = "발행쿠폰 ID는 필수값 입니다.")
    private Long couponPublishId; // 사용할 발행쿠폰 ID

    @NotNull(message = "주문 상품 리스트는 필수값 입니다.")
    private List<OrderItemRequest> orderItemList;

    public OrderCreateCommand toCommand() {
        // 주문 상품 리스트 변환
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();
        for (OrderItemRequest itemRequest : orderItemList) {
            orderItemInfos.add(itemRequest.toOrderItemInfo());
        }

        return OrderCreateCommand.builder()
                .userId(this.userId)
                .couponPublishId(this.couponPublishId)
                .orderItems(new OrderItems(orderItemInfos)).build();
    }
}
