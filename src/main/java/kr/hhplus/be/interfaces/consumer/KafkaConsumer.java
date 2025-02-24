package kr.hhplus.be.interfaces.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = "test-topic", groupId = "group-id")
    public Object listen(ConsumerRecord<String, Object> record) {
        System.out.println("Received Message: " + record.value());
        return record.value();
    }
}
