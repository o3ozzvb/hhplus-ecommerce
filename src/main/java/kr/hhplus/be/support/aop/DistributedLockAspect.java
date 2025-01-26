package kr.hhplus.be.support.aop;


import kr.hhplus.be.support.aop.annotation.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * @RedissonLock 선언 시 수행되는 Aop class
 */
@Order(Ordered.HIGHEST_PRECEDENCE) // 우선순위를 통해 @Transactional 보다 lock 이 먼저 걸리는 것을 보장.
@Aspect
@Component
@Slf4j
public class DistributedLockAspect {

    private final RedissonClient redisson;

    private static final String LOCK_PREFIX = ":";

    public DistributedLockAspect(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Around("@annotation(kr.hhplus.be.support.aop.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // lock key -> 메서드명:키값
        String key = CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.value()).toString();
        String lockKey = method.getName() + LOCK_PREFIX + key;
        RLock rLock = redisson.getLock(lockKey);

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            log.info("lock : {}", lockKey);
            if (!available) {
                return false;
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            // 락 해제
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("unlock : {}", lockKey);
            }
        }
    }
}
