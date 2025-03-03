package kr.hhplus.be.application.payment;

import kr.hhplus.be.application.payment.dto.PaymentCommand;
import kr.hhplus.be.domain.order.entity.Order;
import kr.hhplus.be.domain.order.enumtype.OrderStatus;
import kr.hhplus.be.domain.order.repository.OrderRepository;
import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PaymentConcurrencyTest {

    @Autowired
    PaymentFacade paymentFacade;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("잔액이 10만원인 사용자가 7만원 결제를 동시에 2번 시도 하면 1번의 결제만 성공한다. 그리고 검증에서는 잔액이 3만원임을 검증한다")
    public void 결제_잔액_동시성테스트() throws Exception {
        // given
        // 사용자 데이터 세팅
        BigDecimal balance = BigDecimal.valueOf(100000);
        User user = new User(null, "김유저", balance, LocalDateTime.now(), LocalDateTime.now());
        User savedUser = userRepository.save(user);
        // 주문 정보 세팅
        long userId = 1L;
        BigDecimal orderAmount = BigDecimal.valueOf(130000);
        Order orderInfo = new Order(null, userId, null, LocalDateTime.now(), orderAmount, BigDecimal.ZERO, orderAmount, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Order order = orderRepository.save(orderInfo);

        BigDecimal payAmount = BigDecimal.valueOf(70000);

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    PaymentCommand command = PaymentCommand.builder()
                            .orderId(1L)
                            .userId(savedUser.getId())
                            .payAmount(payAmount)
                            .build();
                    paymentFacade.payment(command);
                    successCount.incrementAndGet();  // 성공 카운트 증가
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();  // latch 감소
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);  // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown();

        // then
        User findUser = userRepository.findById(savedUser.getId());
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(findUser.getBalance().setScale(0, RoundingMode.DOWN)).isEqualTo(balance.subtract(payAmount)); // 잔액 검증
    }
}
