package club.p6e.coat.auth.web.reactive.cache.redis;

import club.p6e.coat.auth.web.reactive.cache.redis.support.RedisCache;
import club.p6e.coat.auth.web.reactive.cache.VoucherCache;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

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
public class VoucherRedisCache extends RedisCache implements VoucherCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public VoucherRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String key) {
        final String nk = CACHE_PREFIX + key;
        return template.delete(nk).map(l -> nk);
    }

    @Override
    public Mono<Map<String, String>> get(String key) {
        System.out.println("  >>> >> " + key);
        return template
                .opsForHash()
                .entries(CACHE_PREFIX + key)
                .collectList()
                .map(list -> {
                    System.out.println(list);
                    final Map<String, String> map = new HashMap<>(list.size());
                    list.forEach(item -> map.put((String) item.getKey(), (String) item.getValue()));
                    return map;
                });
    }

    @Override
    public Mono<String> set(String key, Map<String, String> data) {
        final String nk = CACHE_PREFIX + key;
        return template
                .opsForHash()
                .putAll(nk, data)
                .flatMap(b -> b ? template.expire(nk, Duration.of(
                        EXPIRATION_TIME, ChronoUnit.SECONDS)).map(bb -> nk) : Mono.empty());
    }

}
