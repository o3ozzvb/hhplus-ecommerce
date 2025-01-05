package kr.hhplus.be.interfaces.dto.order;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderResponse {
    private long orderId;
    private int totalAmount;
    private LocalDateTime orderDateTime;
}
