package kr.hhplus.be.support;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCleanup {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCleanup(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Redis flushAll()을 실행해 데이터 전체를 삭제한다.
     */
    public void execute() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

}