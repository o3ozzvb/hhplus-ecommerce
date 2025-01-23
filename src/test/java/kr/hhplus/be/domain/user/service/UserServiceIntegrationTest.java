package kr.hhplus.be.domain.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
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
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Test
    @DisplayName("트랜잭션 B에서 잔액 차감 후, 차감 전 조회한 사용자의 데이터로 잔액 차감을 할 경우 OptimisticLockException이 발생한다.")
    public void testOptimisticLock() throws InterruptedException {
        User user = userRepository.save(User.of("김유저", BigDecimal.valueOf(10000)));

        // 트랜잭션 B: 데이터를 미리 조회 (트랜잭션 A 커밋 전)
        User initialData = userRepository.findById(user.getId());

        // 트랜잭션 A: 다른 스레드에서 데이터 수정
        executorService.submit(() -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                User findUser = em.find(User.class, user.getId());
                findUser.useBalance(BigDecimal.valueOf(5000));
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
        User initialUserData = userRepository.findById(user.getId());
        Thread.sleep(500); // 트랜잭션 A가 완료되도록 대기
        assertThrows(OptimisticLockException.class, () -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                User findUser = em.merge(initialData); // 트랜잭션 A 이전의 데이터를 기반으로 작업
                findUser.useBalance(BigDecimal.valueOf(4000)); // 잔액 차감
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
