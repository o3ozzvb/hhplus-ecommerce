package kr.hhplus.be.application.outbox;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import kr.hhplus.be.domain.outbox.entity.PaymentOutbox;
import kr.hhplus.be.domain.outbox.enumtype.OutboxStatus;
import kr.hhplus.be.domain.outbox.repository.OutboxRepository;
import kr.hhplus.be.infrastructure.kafka.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class PaymentOutboxFacadeIntegrationTest {

    @Autowired
    private PaymentOutboxFacade outboxFacade;

    @Autowired
    private OutboxRepository outboxRepository;

    @MockitoBean
    private KafkaProducer kafkaProducer;  // 실제 Kafka 대신 Mock 처리

    @BeforeEach
    void setup() {
        // 주문 정보
        long orderId1 = 111L;
        long orderId2 = 222L;
        List<OrderItemInfo> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItemInfo(1L, 1, BigDecimal.valueOf(10000)));
        orderItemList.add(new OrderItemInfo(2L, 2, BigDecimal.valueOf(50000)));
        OrderInfo orderInfo1 = new OrderInfo(orderId1, 1L, null, BigDecimal.valueOf(60000), BigDecimal.ZERO, BigDecimal.valueOf(60000), LocalDateTime.now(), new OrderItems(orderItemList));
        OrderInfo orderInfo2 = new OrderInfo(orderId2, 2L, null, BigDecimal.valueOf(100000), BigDecimal.ZERO, BigDecimal.valueOf(100000), LocalDateTime.now(), new OrderItems(orderItemList));

        // 테스트용 Outbox 이벤트 2개 저장
        outboxRepository.save(PaymentOutbox.of(String.valueOf(orderId1), OutboxStatus.INIT, orderInfo1));
        outboxRepository.save(PaymentOutbox.of(String.valueOf(orderId2), OutboxStatus.INIT, orderInfo2));
    }

    @Test
    @DisplayName("Kafka 발행 후 Outbox 이벤트 상태가 SUCCESS로 업데이트된다.")
    void publishUnprocessedEvents() {
        // Mock 설정: Kafka가 정상적으로 메시지를 발행했다고 가정
        doNothing().when(kafkaProducer).send(anyString(), anyString(), anyString());

        // When
        outboxFacade.publishUnsuccssedEvents();

        // Then
        List<PaymentOutbox> events = outboxRepository.findAll();
        assertThat(events).allMatch(event -> event.getStatus() == OutboxStatus.SUCCESS);

        // Kafka가 2번 호출되었는지 검증
        verify(kafkaProducer, times(2)).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Kafka 발행 실패 시 Outbox 이벤트 상태가 FAILED로 업데이트된다")
    void publishFailingEvents() {
        // Mock 설정: Kafka 발행 실패 시 예외 발생
        doThrow(new RuntimeException("Kafka 오류")).when(kafkaProducer).send(anyString(), anyString(), anyString());

        // When
        outboxFacade.publishUnsuccssedEvents();

        // Then
        List<PaymentOutbox> events = outboxRepository.findUnSuccessedEventList(10);
        assertThat(events).allMatch(event -> event.getStatus() == OutboxStatus.FAIL);
    }
}
