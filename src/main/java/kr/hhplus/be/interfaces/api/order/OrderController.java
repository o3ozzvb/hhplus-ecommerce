package kr.hhplus.be.interfaces.api.order;

import jakarta.validation.Valid;
import kr.hhplus.be.interfaces.dto.common.ApiResponse;
import kr.hhplus.be.interfaces.dto.order.OrderItemInfo;
import kr.hhplus.be.interfaces.dto.order.OrderRequest;
import kr.hhplus.be.interfaces.dto.order.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class OrderController {
    /**
     * 주문 요청
     */
    @PostMapping("/orders")
    public ApiResponse<OrderResponse> order(@RequestBody @Valid OrderRequest request) {
        log.debug("OrderController order - request: {}", request);

        int totalAmount = 0;
        for(OrderItemInfo item : request.getOrderItemList()) {
            totalAmount += item.getQuantity() * item.getPrice();
        }

        OrderResponse response = OrderResponse.builder()
                .orderId(1)
                .totalAmount(totalAmount)
                .orderDateTime(LocalDateTime.now())
                .build();

        return ApiResponse.success(response);
    }
}
