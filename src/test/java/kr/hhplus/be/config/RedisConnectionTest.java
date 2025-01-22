package kr.hhplus.be.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("redis-test")
@SpringBootTest
public class RedisConnectionTest extends TestRedisConfiguration {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testRedisConnection() {
        // Given
        String key = "testKey";
        String value = "testValue";

        // When
        redisTemplate.opsForValue().set(key, value); // Redis에 데이터 저장
        String retrievedValue = redisTemplate.opsForValue().get(key); // 데이터 조회

        // Then
        assertThat(retrievedValue).isEqualTo(value); // 값 검증
        System.out.println("Retrieved value from Redis: " + retrievedValue);
    }

}
