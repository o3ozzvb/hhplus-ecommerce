package kr.hhplus.be.infrastructure.coupon.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CouponRedisRepository {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String COUPON_QUANTITY = "coupon:quantity";

    /**
     * 쿠폰의 잔여수량 적재
     */
    public void cacheCouponQuantity(Long couponId, Integer quantity) {
        redisTemplate.opsForHash().put(COUPON_QUANTITY, String.valueOf(couponId), String.valueOf(quantity));
    }

    /**
     * 쿠폰 잔여수량 조회
     */
    public Integer getCouponRemainQuantity(Long couponId) {
        Object remainQuantity = redisTemplate.opsForHash().get(COUPON_QUANTITY, String.valueOf(couponId));
        if (remainQuantity != null) {
            return Integer.valueOf((String) remainQuantity);
        }
        return null;
    }

    public void decreaseRemainQuantity(Long couponId) {
        redisTemplate.opsForHash().increment(COUPON_QUANTITY, String.valueOf(couponId), -1);
    }
}
