package kr.hhplus.be.domain.coupon.service;

import kr.hhplus.be.DatabaseCleanup;
import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.coupon.entity.Coupon;
import kr.hhplus.be.domain.coupon.enumtype.DiscountType;
import kr.hhplus.be.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class CouponConcurrencyTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void cleanUpDatabase() {
        databaseCleanup.execute();
    }

    @Test
    @DisplayName("쿠폰 최대 수량이 10개 일 때 사용자 20명이 동시에 쿠폰을 발행하면 10명만 성공한다.")
    public void 선착순_쿠폰_발급_동시성테스트() throws InterruptedException {
        // given
        // 쿠폰 데이터 세팅
        Coupon coupon = Coupon.of("쿠폰", DiscountType.FIXED_RATE, 10, 10);
        Coupon savedCoupon = couponRepository.save(coupon);

        int numberOfThreads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        for (int i = 0; i < numberOfThreads; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    CouponPublishDTO publishDTO = CouponPublishDTO.builder()
                            .couponId(savedCoupon.getId())
                            .userId(userId)
                            .validStartDate(LocalDate.now())
                            .validEndDate(LocalDate.now().plusDays(30)).build();
                    couponService.publishCoupon(publishDTO);
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
        assertThat(successCount.get()).isEqualTo(10);  // 성공 발행 수는 쿠폰 잔여수량과 동일해야 함
        assertThat(failCount.get()).isEqualTo(10);  // 실패 발행 수 검증
        Coupon findCoupon = couponRepository.findById(savedCoupon.getId());
        assertThat(findCoupon.getRemainQuantity()).isEqualTo(0); // 잔여 수량은 0
    }
}
