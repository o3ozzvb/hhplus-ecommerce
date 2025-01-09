package kr.hhplus.be.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.hhplus.be.application.order.OrderFacade;
import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.interfaces.dto.common.ApiResponse;
import kr.hhplus.be.interfaces.dto.order.OrderRequest;
import kr.hhplus.be.interfaces.dto.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderFacade orderFacade;

    /**
     * 주문 요청
     */
    @Operation(summary = "주문 요청", description = "선택 상품에 대한 주문을 요청합니다.")
    @PostMapping("/orders")
    public ApiResponse<OrderResponse> order(@RequestBody @Valid OrderRequest request) {
        log.debug("OrderController order - request: {}", request);

        OrderInfo orderInfo = orderFacade.order(request.toCommand());
        return ApiResponse.success(OrderResponse.from(orderInfo));
    }
}
