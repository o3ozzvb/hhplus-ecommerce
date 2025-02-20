package kr.hhplus.be.interfaces.event;

import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.enumtype.OutboxStatus;
import kr.hhplus.be.domain.outbox.service.OutboxService;
import kr.hhplus.be.infrastructure.event.PaymentSuccessEvent;
import kr.hhplus.be.infrastructure.kafka.KafkaProducer;
import kr.hhplus.be.support.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OutboxService outboxService;
    private final KafkaProducer kafkaProducer;

    @Value("${commerce-api.payment.topic-name}")
    String topic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        Long orderId = event.getOrderInfo().getOrderId();
        kafkaProducer.send(topic, orderId.toString(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(PaymentSuccessEvent event) {
        outboxService.save(PaymentOutbox.of(event.getOrderInfo().getOrderId().toString(), OutboxStatus.INIT, JsonUtil.toJson(event)));
    }
}
