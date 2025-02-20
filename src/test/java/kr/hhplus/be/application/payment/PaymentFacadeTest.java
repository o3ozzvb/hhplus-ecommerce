package kr.hhplus.be.application.payment;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.domain.order.service.OrderService;
import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.domain.payment.service.PaymentService;
import kr.hhplus.be.domain.user.service.UserService;
import kr.hhplus.be.infrastructure.event.PaymentEventPublisher;
import kr.hhplus.be.infrastructure.event.PaymentSuccessEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeTest {

    @Mock
    private UserService userService;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PaymentEventPublisher eventPublisher;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Captor
    private ArgumentCaptor<PaymentSuccessEvent> eventCaptor;


    @Test
    @DisplayName("결제가 완료되면 이벤트가 정상적으로 발행된다.")
    void paymentSuceessEventPublisherTest() {
        // Given
        long userId = 1L;
        long orderId = 1L;
        BigDecimal totalAmount = BigDecimal.valueOf(100000);

        PaymentCommand command = new PaymentCommand(userId, orderId, totalAmount);
        Payment mockPayment = Payment.ofSuccess(orderId, totalAmount, "transaction_any_id");

        // 주문 정보
        List<OrderItemInfo> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItemInfo(1L, 1, BigDecimal.valueOf(10000)));
        orderItemList.add(new OrderItemInfo(2L, 2, BigDecimal.valueOf(50000)));
        OrderInfo mockOrderInfo = new OrderInfo(orderId, userId, null, totalAmount, BigDecimal.ZERO, totalAmount, LocalDateTime.now(), new OrderItems(orderItemList));

        when(paymentService.pay(anyLong(), any())).thenReturn(mockPayment);
        when(orderService.getOrderInfo(anyLong())).thenReturn(mockOrderInfo);

        // When
        paymentFacade.payment(command);

        // Then
        verify(eventPublisher, times(1)).send(eventCaptor.capture());
        PaymentSuccessEvent publishedEvent = eventCaptor.getValue();

        assertThat(publishedEvent).isNotNull();
        assertThat(publishedEvent.getOrderInfo()).isEqualTo(mockOrderInfo);
    }
}
