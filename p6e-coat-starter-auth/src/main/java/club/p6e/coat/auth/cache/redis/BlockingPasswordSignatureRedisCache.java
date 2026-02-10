package club.p6e.coat.auth.cache.redis;

import club.p6e.coat.auth.cache.BlockingPasswordSignatureCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Blocking Password Signature Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.cache.redis.BlockingPasswordSignatureRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingPasswordSignatureRedisCache implements BlockingPasswordSignatureCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public BlockingPasswordSignatureRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void del(String key) {
        template.delete(CACHE_PREFIX + key);
    }

    @Override
    public String get(String key) {
        return template.opsForValue().get(CACHE_PREFIX + key);
    }

    @Override
    public void set(String key, String value) {
        template.opsForValue().set(CACHE_PREFIX + key, value, Duration.ofSeconds(EXPIRATION_TIME));
    }

}
