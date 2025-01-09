package kr.hhplus.be.interfaces.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.hhplus.be.application.payment.PaymentFacade;
import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.interfaces.dto.common.ApiResponse;
import kr.hhplus.be.interfaces.dto.payment.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    /**
     * 결제 요청
     */
    @Operation(summary = "결제 요청", description = "주문에 대한 결제를 요청합니다.")
    @PostMapping("/payments")
    public ApiResponse<Payment> payment(@RequestBody @Valid PaymentRequest request) {
        paymentFacade.payment(request.toPaymentCommand());

        return ApiResponse.success(null);
    }
}
