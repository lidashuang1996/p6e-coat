package club.p6e.coat.auth.web.cache.redis.support;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public abstract class AbstractRedisCache {

    /**
     * Delete Verification Code
     *
     * @param template String Redis Template Object
     * @param key      Key
     */
    public void delVerificationCode(StringRedisTemplate template, String key) {
        template.delete(key);
    }

    /**
     * Get Verification Code
     *
     * @param template String Redis Template Object
     * @param key      Key
     * @return Verification Code List
     */
    public List<String> getVerificationCode(StringRedisTemplate template, String key) {
        final long now = System.currentTimeMillis();
        final List<String> list = new ArrayList<>();
        final Map<Object, Object> data = template.opsForHash().entries(key);
        for (final Object k : data.keySet()) {
            try {
                if (now > Long.parseLong(String.valueOf(data.get(k)))) {
                    template.opsForHash().delete(key, k);
                } else {
                    list.add(String.valueOf(k));
                }
            } catch (Exception e) {
                // ignore exception
            }
        }
        return list;
    }

    /**
     * Set Verification Code
     *
     * @param template   String Redis Template Object
     * @param key        Key
     * @param value      Value
     * @param expiration Expiration Time
     */
    public void setVerificationCode(StringRedisTemplate template, String key, String value, long expiration) {
        final String timestamp = String.valueOf(System.currentTimeMillis() + expiration * 1000L);
        template.opsForHash().put(key, value, timestamp);
        template.expire(key, Duration.of(expiration, ChronoUnit.SECONDS));
    }

}
