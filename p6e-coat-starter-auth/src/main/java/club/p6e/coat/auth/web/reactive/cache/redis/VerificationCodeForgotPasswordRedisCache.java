package club.p6e.coat.auth.web.reactive.cache.redis;

import club.p6e.coat.auth.web.reactive.cache.VerificationCodeForgotPasswordCache;
import club.p6e.coat.auth.web.reactive.cache.redis.support.RedisCache;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Verification Code Forgot Password Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeForgotPasswordRedisCache
        extends RedisCache implements VerificationCodeForgotPasswordCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public VerificationCodeForgotPasswordRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String key) {
        return delVerificationCode(template, CACHE_PREFIX + key);
    }

    @Override
    public Mono<List<String>> get(String key) {
        return getVerificationCode(template, CACHE_PREFIX + key, EXPIRATION_TIME);
    }

    @Override
    public Mono<String> set(String key, String value) {
        return setVerificationCode(template, CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

}
