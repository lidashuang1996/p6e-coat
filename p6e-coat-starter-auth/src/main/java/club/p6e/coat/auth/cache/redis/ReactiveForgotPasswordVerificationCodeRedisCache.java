package club.p6e.coat.auth.cache.redis;

import club.p6e.coat.auth.cache.ReactiveForgotPasswordVerificationCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Reactive Forgot Password Verification Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.cache.redis.ReactiveForgotPasswordVerificationCodeRedisCache")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveForgotPasswordVerificationCodeRedisCache implements ReactiveForgotPasswordVerificationCodeCache {

    /**
     * Code Redis Cache Object
     */
    private final ReactiveCodeRedisCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Reactive String Redis Template Object
     */
    public ReactiveForgotPasswordVerificationCodeRedisCache(ReactiveCodeRedisCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<String> del(String key) {
        return cache.delVerificationCode(CACHE_PREFIX + key);
    }

    @Override
    public Mono<List<String>> get(String key) {
        return cache.getVerificationCode(CACHE_PREFIX + key);
    }

    @Override
    public Mono<String> set(String key, String value) {
        return cache.setVerificationCode(CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

}
