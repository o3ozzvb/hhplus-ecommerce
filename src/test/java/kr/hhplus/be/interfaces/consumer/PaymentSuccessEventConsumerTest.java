package kr.hhplus.be.interfaces.consumer;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.enumtype.OutboxStatus;
import kr.hhplus.be.domain.outbox.repository.OutboxRepository;
import kr.hhplus.be.infrastructure.kafka.KafkaProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class PaymentSuccessEventConsumerTest {

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    OutboxRepository outboxRepository;

    @Value("${commerce-api.payment.topic-name}")
    String topic;

    @Test
    @DisplayName("결제 성공 이벤트가 발행되면 outbox의 상태가 SUCCESS로 업데이트 된다.")
    public void paymentSuccessEventConsumerTest() {
        //given
        long orderId = 1L;
        long userId = 1L;
        BigDecimal totalAmount = BigDecimal.valueOf(100000);

        List<OrderItemInfo> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItemInfo(1L, 1, BigDecimal.valueOf(10000)));
        orderItemList.add(new OrderItemInfo(2L, 2, BigDecimal.valueOf(50000)));
        OrderInfo orderInfo = new OrderInfo(orderId, userId, null, totalAmount, BigDecimal.ZERO, totalAmount, LocalDateTime.now(), new OrderItems(orderItemList));

        PaymentOutbox outbox = PaymentOutbox.of(topic, OutboxStatus.INIT, orderInfo);
        PaymentOutbox savedOutbox = outboxRepository.save(outbox);

        // when
        kafkaProducer.send(topic, savedOutbox.getMessageId(), savedOutbox.getPayload());

        // then
        await().pollInterval(300, TimeUnit.MILLISECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(outboxRepository.findById(savedOutbox.getMessageId()).getStatus()).isEqualTo(OutboxStatus.SUCCESS));
    }

}
