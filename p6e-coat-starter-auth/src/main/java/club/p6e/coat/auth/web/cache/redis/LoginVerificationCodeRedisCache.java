package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.web.cache.LoginVerificationCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Login Verification Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(LoginVerificationCodeCache.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class LoginVerificationCodeRedisCache implements LoginVerificationCodeCache {

    /**
     * Code Redis Cache Object
     */
    private final CodeRedisCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Code Redis Cache Object
     */
    public LoginVerificationCodeRedisCache(CodeRedisCache cache) {
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
