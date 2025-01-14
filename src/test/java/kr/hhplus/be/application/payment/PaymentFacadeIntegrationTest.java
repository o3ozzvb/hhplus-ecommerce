package kr.hhplus.be.application.payment;

import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.application.platform.PlatformService;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    /**
     * 결제
     * 1. 잔액 확인/차감
     * 2. 결제 정보 저장
     */
    @Test
    void 결제_통합테스트() {
        // given
        // 사용자 정보 세팅
        int balance = 150000;
        userRepository.save(new User(null, "김유저", balance, LocalDateTime.now(), LocalDateTime.now()));
        // 주문 정보 세팅
        long userId = 1L;
        int orderAmount = 130000;
        Order orderInfo = new Order(null, userId, null, LocalDateTime.now(), orderAmount, 0, orderAmount, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Order order = orderRepository.save(orderInfo);
        OrderDetail orderDetail1 = new OrderDetail(null, order.getId(), 1L, 5, 10000, 50000);
        OrderDetail orderDetail2 = new OrderDetail(null, order.getId(), 2L, 4, 20000, 80000);
        orderDetailRepository.save(orderDetail1);
        orderDetailRepository.save(orderDetail2);

        // when
        PaymentCommand command = PaymentCommand.builder()
                .orderId(order.getId())
                .userId(userId)
                .payAmount(orderAmount)
                .build();

        // when
        paymentFacade.payment(command);

        // then
        // 잔액 검증
        User findUser = userRepository.findById(userId);
        assertThat(findUser.getBalance()).isEqualTo(balance - orderAmount);
    }
}