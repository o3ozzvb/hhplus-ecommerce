package kr.hhplus.be.domain.payment.repository;

import kr.hhplus.be.domain.payment.entity.Payment;

public interface PaymentRepository {
    /**
     * 결제 정보 저장
     */
    Payment save(Payment payment);

    Payment findById(long id);
}
