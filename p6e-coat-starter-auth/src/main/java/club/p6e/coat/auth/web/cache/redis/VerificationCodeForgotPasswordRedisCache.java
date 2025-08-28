package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.web.cache.VerificationCodeForgotPasswordCache;
import club.p6e.coat.auth.web.cache.redis.support.AbstractRedisCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Verification Code Forgot Password Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = VerificationCodeForgotPasswordCache.class,
        ignored = VerificationCodeForgotPasswordRedisCache.class
)
public class VerificationCodeForgotPasswordRedisCache
        extends AbstractRedisCache implements VerificationCodeForgotPasswordCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public VerificationCodeForgotPasswordRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void del(String key) {
        delVerificationCode(template, CACHE_PREFIX + key);
    }

    @Override
    public List<String> get(String key) {
        return getVerificationCode(template, CACHE_PREFIX + key, EXPIRATION_TIME);
    }

    @Override
    public void set(String key, String value) {
        setVerificationCode(template, CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

}
