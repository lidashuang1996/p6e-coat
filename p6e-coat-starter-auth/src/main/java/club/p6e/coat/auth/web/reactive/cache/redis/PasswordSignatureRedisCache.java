package club.p6e.coat.auth.web.reactive.cache.redis;

import club.p6e.coat.auth.web.reactive.cache.redis.support.RedisCache;
import club.p6e.coat.auth.web.reactive.cache.PasswordSignatureCache;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Password Signature Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class PasswordSignatureRedisCache extends RedisCache implements PasswordSignatureCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public PasswordSignatureRedisCache(ReactiveStringRedisTemplate template) {
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
