package club.p6e.coat.auth.cache.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reactive Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveCodeRedisCache.class,
        ignored = ReactiveCodeRedisCache.class
)
@Component("club.p6e.coat.auth.cache.redis.ReactiveCodeRedisCache")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveCodeRedisCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public ReactiveCodeRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    /**
     * Del Verification Code
     *
     * @param key Key
     * @return Key
     */
    public Mono<String> delVerificationCode(String key) {
        return template.delete(key).map(l -> key);
    }

    /**
     * Get Verification Code
     *
     * @param key Key
     * @return Verification Code List
     */
    public Mono<List<String>> getVerificationCode(String key) {
        final long now = System.currentTimeMillis();
        return template
                .opsForHash()
                .entries(key)
                .collectList()
                .map(list -> {
                    final Map<String, String> map = new HashMap<>();
                    list.forEach(item -> {
                        final String k = (String) item.getKey();
                        final String v = (String) item.getValue();
                        try {
                            if (now <= Long.parseLong(v)) {
                                map.put(k, v);
                            }
                        } catch (Exception e) {
                            // ignore exception
                        }
                    });
                    return map;
                })
                .flatMap(m -> template
                        .getExpire(key)
                        .flatMap(d -> template
                                .delete(key)
                                .flatMap(l -> template.opsForHash().putAll(key, m))
                                .flatMap(b -> template.expire(key, d))
                                .map(b -> new ArrayList<>(m.keySet()))
                        )
                );
    }

    /**
     * Set Verification Code
     *
     * @param key        Key
     * @param value      Value
     * @param expiration Expiration Time
     * @return Verification Code
     */
    public Mono<String> setVerificationCode(String key, String value, long expiration) {
        final String result = key + "@" + value;
        final String timestamp = String.valueOf(System.currentTimeMillis() + expiration * 1000L);
        return template.opsForHash().put(key, value, timestamp)
                .flatMap(b -> b ? template.expire(key,
                        Duration.of(expiration, ChronoUnit.SECONDS)).map(bb -> result) : Mono.empty());
    }

}
