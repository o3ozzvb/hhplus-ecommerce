package kr.hhplus.be.infrastructure.payment;

import kr.hhplus.be.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository  extends JpaRepository<Payment, Long> {
}
