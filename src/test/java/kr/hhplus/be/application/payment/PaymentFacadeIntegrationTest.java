package kr.hhplus.be.application.payment;

import kr.hhplus.be.application.order.dto.OrderInfo;
import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.entity.OrderDetail;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import kr.hhplus.be.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.payment.entity.Payment;
import kr.hhplus.be.domain.payment.enumtype.PaymentStatus;
import kr.hhplus.be.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.infrastructure.order.DataPlatformClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PaymentFacadeIntegrationTest {

    @Autowired
    PaymentFacade paymentFacade;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    DataPlatformClient dataPlatformClient;

    /**
     * 결제
     * 1. 잔액 확인/차감
     * 2. 결제 정보 저장
     */
    @Test
    void 결제_통합테스트() {
        // given
        // 사용자 정보 세팅
        BigDecimal balance = new BigDecimal(150000);
        userRepository.save(new User(null, "김유저", balance, LocalDateTime.now(), LocalDateTime.now()));
        // 주문 정보 세팅
        long userId = 1L;
        BigDecimal orderAmount = BigDecimal.valueOf(130000);
        Order orderInfo = new Order(null, userId, null, LocalDateTime.now(), orderAmount, BigDecimal.ZERO, orderAmount, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Order order = orderRepository.save(orderInfo);
        OrderDetail orderDetail1 = new OrderDetail(null, order.getId(), 1L, 5, BigDecimal.valueOf(10000), BigDecimal.valueOf(50000));
        OrderDetail orderDetail2 = new OrderDetail(null, order.getId(), 2L, 4, BigDecimal.valueOf(20000), BigDecimal.valueOf(80000));
        orderDetailRepository.save(orderDetail1);
        orderDetailRepository.save(orderDetail2);

        // when
        PaymentCommand command = PaymentCommand.builder()
                .orderId(order.getId())
                .userId(userId)
                .payAmount(orderAmount)
                .build();

        // when
        Payment payment = paymentFacade.payment(command);

        // then
        // 잔액 검증
        User findUser = userRepository.findById(userId);
        assertThat(findUser.getBalance().compareTo(balance.subtract(orderAmount))).isEqualTo(0);

        // 저장된 결제 정보 검증
        Payment findPayment = paymentRepository.findById(payment.getId());
        assertThat(findPayment.getRefOrderId()).isEqualTo(order.getId());
        assertThat(findPayment.getPayAmount().compareTo(orderAmount)).isEqualTo(0);
        assertThat(findPayment.getPayDate()).isEqualTo(LocalDate.now());
        assertThat(findPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        // 주문 상태 검증
        Order findOrder = orderRepository.findById(payment.getRefOrderId());
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        // 데이터 플랫폼 전송 검증
        OrderInfo info = OrderInfo.from(order, List.of(orderDetail1, orderDetail2));
        assertThat(dataPlatformClient.send(info)).isTrue();
    }
}