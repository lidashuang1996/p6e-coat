package club.p6e.coat.auth.cache.redis;

import club.p6e.coat.auth.cache.ReactiveLoginQuickResponseCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Reactive Login Quick Response Code Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.cache.redis.ReactiveLoginQuickResponseCodeRedisCache")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginQuickResponseCodeRedisCache implements ReactiveLoginQuickResponseCodeCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public ReactiveLoginQuickResponseCodeRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String key) {
        final String nk = CACHE_PREFIX + key;
        return template.delete(nk).map(l -> nk);
    }

    @Override
    public Mono<String> get(String key) {
        return template.opsForValue().get(CACHE_PREFIX + key);
    }

    @Override
    public Mono<String> set(String key, String value) {
        final String nk = CACHE_PREFIX + key;
        return template.opsForValue().set(nk, value, Duration.ofSeconds(EXPIRATION_TIME)).map(b -> nk);
    }

}
