package kr.hhplus.be.domain.outbox.repository;

import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;

public interface OutboxRepository {

    PaymentOutbox save(PaymentOutbox outbox);

    PaymentOutbox findById(String id);
}
