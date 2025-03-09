package club.p6e.coat.auth.client;

import club.p6e.coat.auth.client.cache.AuthCache;
import club.p6e.coat.auth.client.cache.AuthCacheReactive;
import club.p6e.coat.auth.client.cache.AuthStateCache;
import club.p6e.coat.auth.client.cache.AuthStateCacheReactive;
import club.p6e.coat.auth.client.cache.redis.AuthRedisCache;
import club.p6e.coat.auth.client.cache.redis.AuthRedisCacheReactive;
import club.p6e.coat.auth.client.cache.redis.AuthStateRedisCache;
import club.p6e.coat.auth.client.cache.redis.AuthStateRedisCacheReactive;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthClientRedisCacheConfig {

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
    public AuthCache injectionAuthCache(StringRedisTemplate template) {
        return new AuthRedisCache(template);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
    public AuthStateCache injectionAuthStateCache(StringRedisTemplate template) {
        return new AuthStateRedisCache(template);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
    public AuthCacheReactive injectionAuthCacheReactive(ReactiveStringRedisTemplate template) {
        return new AuthRedisCacheReactive(template);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
    public AuthStateCacheReactive injectionAuthStateCacheReactive(ReactiveStringRedisTemplate template) {
        return new AuthStateRedisCacheReactive(template);
    }

}
