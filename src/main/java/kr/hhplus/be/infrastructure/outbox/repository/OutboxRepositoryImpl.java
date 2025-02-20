package kr.hhplus.be.infrastructure.outbox.repository;

import kr.hhplus.be.domain.exception.CommerceNotFoundException;
import kr.hhplus.be.domain.exception.ErrorCode;
import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.repository.OutboxRepository;
import kr.hhplus.be.infrastructure.outbox.jpa.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public PaymentOutbox save(PaymentOutbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public PaymentOutbox findById(String id) {
        return outboxJpaRepository.findById(id).orElseThrow(() -> new CommerceNotFoundException(ErrorCode.OUTBOX_NOT_EXIST));
    }
}
