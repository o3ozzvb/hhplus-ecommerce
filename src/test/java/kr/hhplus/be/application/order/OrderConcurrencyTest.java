package kr.hhplus.be.application.order;

import kr.hhplus.be.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.application.order.dto.OrderItemInfo;
import kr.hhplus.be.application.order.dto.OrderItems;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.be.fixture.ProductFixture.createProduct;
import static kr.hhplus.be.fixture.ProductFixture.createProductInventory;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class OrderConcurrencyTest {

    @Autowired
    OrderFacade orderFacade;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInventoryRepository productInventoryRepository;

    @Test
    @DisplayName("재고가 5개인 상품을 10명이 동시성 주문하면 5명은 주문에 실패한다.")
    public void 주문_재고_동시성테스트() throws InterruptedException {
        // given
        // 상품 데이터 세팅
        Product socks = productRepository.save(createProduct("양말", Category.ETC, BigDecimal.valueOf(5000)));
        // 상품재고 데이터 세팅
        productInventoryRepository.save(createProductInventory(socks.getId(), 5));
        // Order
        OrderItemInfo orderItem = new OrderItemInfo(socks.getId(), 1, socks.getPrice());


        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        for (int i = 0; i < numberOfThreads; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    OrderCreateCommand orderCommand = OrderCreateCommand.builder()
                            .userId(userId)
                            .orderItems(new OrderItems(Arrays.asList(orderItem)))
                            .build();  // 상품 1개씩 주문
                    orderFacade.order(orderCommand);  // 주문 요청
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
        assertThat(successCount.get()).isEqualTo(5);  // 성공 주문 수는 재고량과 동일해야 함
        assertThat(failCount.get()).isEqualTo(5);  // 실패 주문 수 검증
    }
}
