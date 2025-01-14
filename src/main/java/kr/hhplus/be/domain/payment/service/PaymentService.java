package kr.hhplus.be.domain.payment.service;

import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    /**
     * 결제 요청 (결제 정보 저장)
     */
    public Payment pay(long orderId, BigDecimal payAmount) {
        Payment payment = Payment.ofSuccess(orderId, payAmount, generateTransactionId());
        return paymentRepository.save(payment);
    }

    /**
     * 고유 transactionId 생성
     */
    private String generateTransactionId() {
        // UUID를 사용하여 고유 식별자 생성
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

}
