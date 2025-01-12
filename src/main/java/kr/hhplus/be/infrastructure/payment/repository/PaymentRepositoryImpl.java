package kr.hhplus.be.infrastructure.payment.repository;

import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.infrastructure.payment.jpa.PaymentJpaRepository;
import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
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

    @Override
    public Payment findById(long id) {
        return paymentJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_EXIST));
    }
}
