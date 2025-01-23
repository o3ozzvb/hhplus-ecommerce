package kr.hhplus.be.domain.user.service;

import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class UserServiceConcurrencyTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("동일 유저가 동시에 충전 시도하면 1건은 실패 1건은 성공한다.")
    public void 동일유저_충전_동시성테스트() throws InterruptedException {
        // given
        // 유저 데이터 세팅
        User user = userRepository.save(User.of("김유저", BigDecimal.ZERO));

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        for (int i = 0; i < numberOfThreads; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    userService.charge(userId, BigDecimal.valueOf(10000));
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
        assertThat(successCount.get()).isEqualTo(1);  // 성공 수 검증
        assertThat(failCount.get()).isEqualTo(1);  // 실패 수 검증
        User findUser = userRepository.findById(user.getId());
    }
}
