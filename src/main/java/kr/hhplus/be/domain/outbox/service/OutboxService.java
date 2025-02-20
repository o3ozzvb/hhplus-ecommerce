package kr.hhplus.be.domain.outbox.service;

import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public PaymentOutbox save(PaymentOutbox outbox) {
        return outboxRepository.save(outbox);
    }

    public PaymentOutbox getOutboxById(String id) {
        return outboxRepository.findById(id);
    }

    public List<PaymentOutbox> getUnSuccessedEventList(int limit) {
        return outboxRepository.findUnSuccessedEventList(limit);
    }
}
