package kr.hhplus.be.infrastructure.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 캐시 만료 시간 설정 등 추가 설정이 가능
                .entryTtl(Duration.ofDays(1));

        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("topSalesProducts");

        return RedisCacheManager.builder(redisConnectionFactory)
                .initialCacheNames(cacheNames)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}
