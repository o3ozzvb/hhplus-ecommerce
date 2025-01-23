package kr.hhplus.be.domain.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.domain.product.entity.Product;
import kr.hhplus.be.domain.product.entity.ProductInventory;
import kr.hhplus.be.domain.product.enumtype.Category;
import kr.hhplus.be.domain.product.repository.ProductInventoryRepository;
import kr.hhplus.be.domain.product.repository.ProductRepository;
import kr.hhplus.be.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class ProductServiceLockingTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Test
    @DisplayName("트랜잭션 B에서 잔액 차감 후, 차감 전 조회한 사용자의 데이터로 잔액 차감을 할 경우 OptimisticLockException이 발생한다.")
    public void testOptimisticLock() throws InterruptedException {
        Product product = productRepository.save(Product.of("양말", Category.ETC, BigDecimal.valueOf(2000)));
        productInventoryRepository.save(ProductInventory.of(product.getId(), 5));

        // 트랜잭션 B: 데이터를 미리 조회 (트랜잭션 A 커밋 전)
        ProductInventory initialData = productInventoryRepository.findById(product.getId());

        // 트랜잭션 A: 다른 스레드에서 데이터 수정
        executorService.submit(() -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                ProductInventory findInventory = em.find(ProductInventory.class, product.getId());
                productService.deductInventory(product.getId(), 1);
                em.flush(); // 변경사항 즉시 반영

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        });

        // 트랜잭션 B: 커밋 전 조회한 데이터를 기반으로 수정 시도
        productInventoryRepository.findById(product.getId());
        Thread.sleep(500); // 트랜잭션 A가 완료되도록 대기
        assertThrows(OptimisticLockException.class, () -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                ProductInventory findInventory = em.merge(initialData); // 트랜잭션 A 이전의 데이터를 기반으로 작업
                productService.deductInventory(product.getId(), 1);
                em.flush(); // OptimisticLockException 발생
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e; // 예외 재던짐
            } finally {
                em.close();
            }
        });

        executorService.shutdown();
    }
}
