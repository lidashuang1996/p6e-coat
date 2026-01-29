package club.p6e.coat.auth.oauth2.cache.redis;

import club.p6e.coat.auth.oauth2.cache.ReactiveCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Reactive Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveCodeRedisCache.class)
@Component("club.p6e.coat.auth.oauth2.cache.redis.ReactiveCodeRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class ReactiveCodeRedisCache implements ReactiveCodeCache {

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

    @Override
    public Mono<String> del(String key) {
        final String nk = CACHE_PREFIX + key;
        return template.delete(nk).map(l -> nk);
    }

    @Override
    public Mono<Map<String, String>> get(String key) {
        final Map<String, String> result = new HashMap<>();
        return template.opsForHash().entries(CACHE_PREFIX + key)
                .collectList()
                .map(l -> {
                    l.forEach(e -> result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue())));
                    return result;
                });
    }

    @Override
    public Mono<String> set(String key, Map<String, String> value) {
        final String nk = CACHE_PREFIX + key;
        return template.opsForHash().putAll(nk, value)
                .flatMap(b -> template.expire(nk, Duration.ofSeconds(EXPIRATION_TIME)))
                .map(b -> nk);
    }

}
