package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.web.cache.VerificationCodeRegisterCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Register Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = VerificationCodeRegisterCache.class,
        ignored = RegisterCodeRedisCache.class
)
public class RegisterCodeRedisCache implements VerificationCodeRegisterCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public RegisterCodeRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void del(String key) {
        return delVerificationCode(template, CACHE_PREFIX + key);
    }

    @Override
    public List<String> get(String key) {
        return getVerificationCode(template, CACHE_PREFIX + key, EXPIRATION_TIME);
    }

    @Override
    public void set(String key, String value) {
        return setVerificationCode(template, CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

}
