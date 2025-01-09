package kr.hhplus.be.infrastructure.payment;

import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    /**
     * 결제 정보 저장
     */
    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
