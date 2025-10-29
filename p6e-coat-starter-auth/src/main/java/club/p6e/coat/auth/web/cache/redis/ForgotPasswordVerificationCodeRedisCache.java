package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.web.cache.ForgotPasswordVerificationCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Forgot Password Verification Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ForgotPasswordVerificationCodeCache.class,
        ignored = ForgotPasswordVerificationCodeRedisCache.class
)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@Component("club.p6e.coat.auth.web.cache.redis.ForgotPasswordVerificationCodeRedisCache")
public class ForgotPasswordVerificationCodeRedisCache implements ForgotPasswordVerificationCodeCache {

    /**
     * Code Redis Cache Object
     */
    private final CodeRedisCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Code Redis Cache Object
     */
    public ForgotPasswordVerificationCodeRedisCache(CodeRedisCache cache) {
        this.cache = cache;
    }

    @Override
    public void del(String key) {
        cache.delVerificationCode(CACHE_PREFIX + key);
    }

    @Override
    public List<String> get(String key) {
        return cache.getVerificationCode(CACHE_PREFIX + key);
    }

    @Override
    public void set(String key, String value) {
        cache.setVerificationCode(CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

}
