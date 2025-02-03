package kr.hhplus.be.infrastructure.coupon.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.domain.coupon.dto.CouponPublishDTO;
import kr.hhplus.be.domain.exception.CommerceConflictException;
import kr.hhplus.be.domain.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class CouponPublishRedisRepositoryImpl implements CouponPublishRedisRepository {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String COUPON_KEY_PREFIX = "coupon:pending:";

    /**
     * 쿠폰 발급 요청 대기열 저장
     */
    public void savePublishRequest(CouponPublishDTO publishDTO) {
        String key = COUPON_KEY_PREFIX + publishDTO.getCouponId();
        try {
            String jsonPublishDTO = objectMapper.writeValueAsString(publishDTO);
            log.info("savePublishRequest jsonPublishDTO:{}", jsonPublishDTO);
            redisTemplate.opsForZSet().add(key, jsonPublishDTO, System.currentTimeMillis());
        } catch (JsonProcessingException e) {
            throw new CommerceConflictException(ErrorCode.valueOf("Json parsing error"));
        }
    }

    /**
     * 쿠폰 발급 요청 대기열 꺼내기(POP)
     */
    public List<CouponPublishDTO> getPublishRequest(long couponId, int n) {
        String key = COUPON_KEY_PREFIX + couponId;
        Set<ZSetOperations.TypedTuple<String>> poppedSet = redisTemplate.opsForZSet().popMin(key, n);

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
}
