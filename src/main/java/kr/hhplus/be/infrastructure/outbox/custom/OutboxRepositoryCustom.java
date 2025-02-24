package kr.hhplus.be.infrastructure.outbox.custom;

import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;

import java.util.List;

public interface OutboxRepositoryCustom {

    List<PaymentOutbox> findUnSuccessedEvents(int limit);
}
