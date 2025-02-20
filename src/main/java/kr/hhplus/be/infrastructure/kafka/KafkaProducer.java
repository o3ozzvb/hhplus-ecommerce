package kr.hhplus.be.infrastructure.kafka;

import kr.hhplus.be.support.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void send(String topic, String messageId, Object message) {
        kafkaTemplate.send(topic, messageId, message).whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("[Produce Fail] message: {}", throwable.getMessage(), throwable);
            } else {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("[Produce Success] topic:{}, value: {}, offset: {}",
                        recordMetadata.topic(),
                        result.getProducerRecord().value(),
                        recordMetadata.offset());
            }
        });
    }
}

