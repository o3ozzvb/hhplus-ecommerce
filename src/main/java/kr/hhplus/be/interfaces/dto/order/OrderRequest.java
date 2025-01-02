package kr.hhplus.be.interfaces.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private List<OrderItemInfo> orderItemList;

}
