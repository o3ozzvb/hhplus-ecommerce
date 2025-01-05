package kr.hhplus.be.interfaces.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.hhplus.be.interfaces.dto.common.ApiResponse;
import kr.hhplus.be.interfaces.dto.payment.PaymentRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    /**
     * 결제 요청
     */
    @Operation(summary = "결제 요청", description = "주문에 대한 결제를 요청합니다.")
    @PostMapping("/payments")
    public ApiResponse<Void> payment(@RequestBody @Valid PaymentRequest request) {
        return ApiResponse.success(null);
    }
}
