package kr.hhplus.be.application.event;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import kr.hhplus.be.infrastructure.event.PaymentSuccessEvent;
import kr.hhplus.be.domain.order.client.PlatformClient;
import kr.hhplus.be.interfaces.event.PaymentEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentEventListenerTest {

    @Mock
    private PlatformClient platformClient;

    @InjectMocks
    private PaymentEventListener paymentEventListener;

    @Test
    @DisplayName("결제 완료 이벤트 리스너가 비동기로 정상적으로 실행된다.")
    void paymentSuccessHandlerAsyncTest() {
        // Given
        long userId = 1L;
        long orderId = 1L;
        BigDecimal totalAmount = BigDecimal.valueOf(100000);

        // 주문 정보
        List<OrderItemInfo> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItemInfo(1L, 1, BigDecimal.valueOf(10000)));
        orderItemList.add(new OrderItemInfo(2L, 2, BigDecimal.valueOf(50000)));
        OrderInfo orderInfo = new OrderInfo(orderId, userId, null, totalAmount, BigDecimal.ZERO, totalAmount, LocalDateTime.now(), new OrderItems(orderItemList));

        PaymentSuccessEvent event = new PaymentSuccessEvent(orderInfo);

        // When
        paymentEventListener.paymentSuccessHandler(event);

        // Then
        verify(platformClient, times(1)).send(orderInfo);
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(platformClient, times(1)).send(orderInfo);
        });
    }

}
