package kr.hhplus.be.application.outbox;

import jakarta.transaction.Transactional;
import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.service.OutboxService;
import kr.hhplus.be.infrastructure.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentOutboxFacade {

    private final OutboxService outboxService;
    private final KafkaProducer kafkaProducer;

    private static final int MAX_EVENTS_PER_BATCH = 10;

    @Value("${commerce-api.payment.topic-name}")
    private String topic;

    @Transactional
    public void publishUnsuccssedEvents() {
        List<PaymentOutbox> events = outboxService.getUnSuccessedEventList(MAX_EVENTS_PER_BATCH);

        for (PaymentOutbox event : events) {
            try {
                kafkaProducer.send(topic, event.getMessageId(), event.getPayload());
                event.success();
            } catch (Exception e) {
                event.fail();
            }
            outboxService.save(event);
        }
    }
}
