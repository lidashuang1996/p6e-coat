package club.p6e.coat.auth.web.reactive.cache.redis;

import club.p6e.coat.auth.web.reactive.cache.redis.support.AbstractRedisCache;
import club.p6e.coat.auth.web.reactive.cache.QuickResponseCodeLoginCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Quick Response Code Login Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = QuickResponseCodeLoginCache.class,
        ignored = QuickResponseCodeLoginRedisCache.class
)
public class QuickResponseCodeLoginRedisCache extends AbstractRedisCache implements QuickResponseCodeLoginCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public QuickResponseCodeLoginRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String key) {
        final String nk = CACHE_PREFIX + key;
        return template.delete(nk).map(l -> nk);
    }

    @Override
    public Mono<String> get(String key) {
        System.out.println("duqu  >>> " + CACHE_PREFIX + key);
        return template.opsForValue().get(CACHE_PREFIX + key);
    }

    @Override
    public Mono<String> set(String key, String value) {
        final String nk = CACHE_PREFIX + key;
        System.out.println("NK >>> " + nk + " >>>> " + value);
        return template.opsForValue().set(nk, value, Duration.ofSeconds(EXPIRATION_TIME)).map(b -> nk);
    }

}
