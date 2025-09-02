package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.web.cache.VoucherCache;
import club.p6e.coat.auth.web.cache.redis.support.AbstractRedisCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Voucher Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(value = VoucherCache.class)
public class VoucherRedisCache extends AbstractRedisCache implements VoucherCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public VoucherRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void del(String key) {
        template.delete(CACHE_PREFIX + key);
    }

    @Override
    public Map<String, String> get(String key) {
        final Map<String, String> result = new HashMap<>();
        final Map<Object, Object> data = template.opsForHash().entries(CACHE_PREFIX + key);
        for (final Object k : data.keySet()) {
            final Object v = data.get(k);
            if (k != null && v != null) {
                result.put(String.valueOf(k), String.valueOf(v));
            }
        }
        return result;
    }

    @Override
    public void set(String key, Map<String, String> data) {
        template.opsForHash().putAll(CACHE_PREFIX + key, data);
        template.expire(CACHE_PREFIX + key, Duration.of(EXPIRATION_TIME, ChronoUnit.SECONDS));
    }

}
