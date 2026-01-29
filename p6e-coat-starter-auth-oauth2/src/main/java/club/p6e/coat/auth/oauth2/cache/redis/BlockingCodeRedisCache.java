package club.p6e.coat.auth.oauth2.cache.redis;

import club.p6e.coat.auth.oauth2.cache.BlockingCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Blocking Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingCodeRedisCache.class)
@Component("club.p6e.coat.auth.oauth2.cache.redis.BlockingCodeRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingCodeRedisCache implements BlockingCodeCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public BlockingCodeRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void del(String key) {
        template.delete(CACHE_PREFIX + key);
    }

    @Override
    public Map<String, String> get(String key) {
        final Map<String, String> result = new HashMap<>();
        final Map<Object, Object> value = template.opsForHash().entries(CACHE_PREFIX + key);
        value.forEach((k, v) -> result.put(String.valueOf(k), String.valueOf(v)));
        return result;
    }

    @Override
    public void set(String key, Map<String, String> value) {
        template.opsForHash().putAll(CACHE_PREFIX + key, value);
        template.expire(key, EXPIRATION_TIME, TimeUnit.SECONDS);
    }

}
