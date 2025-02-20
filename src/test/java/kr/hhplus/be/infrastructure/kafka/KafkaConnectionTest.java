package kr.hhplus.be.infrastructure.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class KafkaConnectionTest {

    private static final String TEST_TOPIC = "test-topic";
    private static final AtomicReference<String> receivedMessage = new AtomicReference<>();

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TEST_TOPIC, groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record) {
        receivedMessage.set(record.value());
    }

    @Test
    void testKafkaMessageFlow() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String testMessage = "Kafka Test Message";

        // when
        kafkaTemplate.send(TEST_TOPIC, testMessage);

        // then
        Awaitility.await()
                .pollInterval(300, TimeUnit.MILLISECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    System.out.println("Checking received message...");
                    assertThat(receivedMessage.get()).isEqualTo(testMessage);
                });
    }
}

