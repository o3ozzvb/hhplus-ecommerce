package kr.hhplus.be.infrastructure.coupon.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class CouponPublishRedisRepository {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String COUPON_PUBLISH_REQUEST_PREFIX = "coupon:request";
    private static final String COUPON_PUBLISH_USER_PREFIX = "coupon:";

    /**
     * 쿠폰 발급 요청 대기열 저장
     */
    public void savePublishRequest(CouponPublishDTO publishDTO) {
        try {
            String jsonPublishDTO = objectMapper.writeValueAsString(publishDTO);
            log.info("addPublishRequest jsonPublishDTO: {}", jsonPublishDTO);

            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    // 쿠폰 발급 요청 유저를 Set에 추가
                    String key = COUPON_PUBLISH_USER_PREFIX + publishDTO.getCouponId();
                    operations.opsForSet().add(key, String.valueOf(publishDTO.getUserId()));
                    // 쿠폰 발급 요청 대기열에 Sorted Set으로 추가 (현재 시간을 score로 사용)
                    operations.opsForZSet().add(COUPON_PUBLISH_REQUEST_PREFIX, jsonPublishDTO, System.currentTimeMillis());
                    return null;
                }
            });
        } catch (JsonProcessingException e) {
            throw new CommerceConflictException(ErrorCode.valueOf("Json parsing error"));
        }
    }

    /**
     * 쿠폰 발급 요청 대기열 꺼내기(POP)
     */
    public List<CouponPublishDTO> getPublishRequest(long couponId, int n) {
        Set<ZSetOperations.TypedTuple<String>> poppedSet = redisTemplate.opsForZSet().popMin(COUPON_PUBLISH_REQUEST_PREFIX, n);

        if (poppedSet == null || poppedSet.isEmpty()) {
            return Collections.emptyList();
        }

        return poppedSet.stream()
                .map(tuple -> {
                    String request = tuple.getValue();
                    try {
                        return objectMapper.readValue(request, CouponPublishDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new CommerceConflictException(ErrorCode.valueOf("Json parsing error"));
                    }
                })
                .collect(Collectors.toList());

    }

    /**
     * 쿠폰 발급 유저 조회 (set)
     */
    public boolean isCouponPublishRequested(long couponId, long userId) {
        String key = COUPON_PUBLISH_USER_PREFIX + couponId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, String.valueOf(userId)));
    }

    /**
     * 쿠폰 발급 건 수 조회
     * @return set size
     */
    public Integer getCouponPublishCount(long couponId) {
        String key = COUPON_PUBLISH_USER_PREFIX + couponId;
        return redisTemplate.opsForSet().size(key).intValue();
    }
}
