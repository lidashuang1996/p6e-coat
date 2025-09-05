package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.web.aspect.LoginAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = CodeRedisCache.class,
        ignored = CodeRedisCache.class
)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class CodeRedisCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public CodeRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    /**
     * Del Code
     *
     * @param key Key
     */
    public void delVerificationCode(String key) {
        template.delete(key);
    }

    /**
     * Get Code
     *
     * @param key Key
     * @return Code List
     */
    public List<String> getVerificationCode(String key) {
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
     * Set Code
     *
     * @param key        Key
     * @param value      Value
     * @param expiration Expiration Time
     */
    public void setVerificationCode(String key, String value, long expiration) {
        final String timestamp = String.valueOf(System.currentTimeMillis() + expiration * 1000L);
        template.opsForHash().put(key, value, timestamp);
        template.expire(key, Duration.of(expiration, ChronoUnit.SECONDS));
    }

}
