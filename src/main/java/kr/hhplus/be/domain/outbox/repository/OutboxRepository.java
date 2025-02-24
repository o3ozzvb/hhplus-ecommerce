package kr.hhplus.be.domain.outbox.repository;

import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;

import java.util.List;

public interface OutboxRepository {

    PaymentOutbox save(PaymentOutbox outbox);

    PaymentOutbox findById(String id);

    List<PaymentOutbox> findAll();

    List<PaymentOutbox> findUnSuccessedEventList(int limit);
}
