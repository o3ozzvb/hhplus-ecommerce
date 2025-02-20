package kr.hhplus.be.interfaces.consumer;

import kr.hhplus.be.domain.order.client.PlatformClient;
import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.service.OutboxService;
import kr.hhplus.be.infrastructure.event.PaymentSuccessEvent;
import kr.hhplus.be.support.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessEventConsumer {

    private final OutboxService outboxService;
    private final PlatformClient platformClient;

    @KafkaListener(topics = "${commerce-api.payment.topic-name}", groupId = "payment-group")
    public void sendToPlatform(ConsumerRecord<String, byte[]> record, Acknowledgment acknowledgment) {
        try {
            log.info("[payment-group] value: {}", record.value());
            byte[] messageBytes = record.value();
            String jsonString = new String(messageBytes);
            log.info("[payment-group] jsonString: {}", jsonString);
            PaymentSuccessEvent event = JsonUtil.fromJson(jsonString, PaymentSuccessEvent.class);
            log.info("[payment-group] event: {}", event);
            platformClient.send(event.getOrderInfo());
            log.info("데이터 플랫폼 전송 성공 : {}", event.getOrderInfo());

            // 메시지 정상 처리 완료 후 Offset 커밋
//            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error(e.getMessage());
            PaymentOutbox outbox = outboxService.findById(record.key());
            outboxService.save(outbox.fail());
        }
    }

    @KafkaListener(topics = "${commerce-api.payment.topic-name}", groupId = "payment-outbox")
    public void consumeOutbox(ConsumerRecord<String, byte[]> record) {
        log.info("[payment-outbox] key: {}", record.key());
        PaymentOutbox outbox = outboxService.findById(record.key());
        outboxService.save(outbox.success());
    }

}
