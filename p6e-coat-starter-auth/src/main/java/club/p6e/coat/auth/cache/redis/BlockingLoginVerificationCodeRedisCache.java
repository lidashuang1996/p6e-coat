package club.p6e.coat.auth.cache.redis;

import club.p6e.coat.auth.cache.BlockingLoginVerificationCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Blocking Login Verification Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingLoginVerificationCodeRedisCache.class)
@Component("club.p6e.coat.auth.cache.redis.BlockingLoginVerificationCodeRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginVerificationCodeRedisCache implements BlockingLoginVerificationCodeCache {

    /**
     * Blocking Code Redis Cache Object
     */
    private final BlockingCodeRedisCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Blocking Code Redis Cache Object
     */
    public BlockingLoginVerificationCodeRedisCache(BlockingCodeRedisCache cache) {
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
