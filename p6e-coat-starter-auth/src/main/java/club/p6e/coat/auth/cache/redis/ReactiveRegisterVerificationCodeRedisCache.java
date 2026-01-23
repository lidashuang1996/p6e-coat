package club.p6e.coat.auth.cache.redis;

import club.p6e.coat.auth.cache.ReactiveRegisterVerificationCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Register Verification Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveRegisterVerificationCodeCache.class,
        ignored = ReactiveRegisterVerificationCodeRedisCache.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
@Component("club.p6e.coat.auth.web.reactive.cache.redis.RegisterVerificationCodeRedisCache")
public class ReactiveRegisterVerificationCodeRedisCache implements ReactiveRegisterVerificationCodeCache {

    /**
     * Code Redis Cache Object
     */
    private final ReactiveCodeRedisCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Code Redis Cache Object
     */
    public ReactiveRegisterVerificationCodeRedisCache(ReactiveCodeRedisCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<String> del(String key) {
        return cache.delVerificationCode(CACHE_PREFIX + key);
    }

    @Override
    public Mono<List<String>> get(String key) {
        return cache.getVerificationCode(CACHE_PREFIX + key, EXPIRATION_TIME);
    }

    @Override
    public Mono<String> set(String key, String value) {
        return cache.setVerificationCode(CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

}
