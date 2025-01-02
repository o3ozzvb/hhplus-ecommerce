package kr.hhplus.be.interfaces.api.payment;

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
    @PostMapping("/payment")
    public ApiResponse<Void> payment(@RequestBody @Valid PaymentRequest request) {
        return ApiResponse.success(null);
    }
}
