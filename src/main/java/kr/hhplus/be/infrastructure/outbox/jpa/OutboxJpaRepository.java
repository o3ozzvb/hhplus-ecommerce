package kr.hhplus.be.infrastructure.outbox.jpa;

import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<PaymentOutbox, String> {
}
