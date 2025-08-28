package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.web.cache.VoucherCache;
import club.p6e.coat.auth.web.cache.redis.support.AbstractRedisCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
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
@Component
@ConditionalOnMissingBean(
        value = VoucherCache.class,
        ignored = VoucherRedisCache.class
)
public class VoucherRedisCache extends AbstractRedisCache implements VoucherCache {

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
    public void del(String key) {
        final String nk = CACHE_PREFIX + key;
        return template.delete(nk).map(l -> nk);
    }

    @Override
    public Map<String, String> get(String key) {
        System.out.println("keykeykeykey >>>> " + key);
        return template
                .opsForHash()
                .entries(CACHE_PREFIX + key)
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.empty();
                    }
                    final Map<String, String> map = new HashMap<>(list.size());
                    list.forEach(item -> map.put((String) item.getKey(), (String) item.getValue()));
                    System.out.println("mapmapmap >>> " + map);
                    return Mono.just(map);
                });
    }

    @Override
    public void set(String key, Map<String, String> data) {
        final String nk = CACHE_PREFIX + key;
        return template
                .opsForHash()
                .putAll(nk, data)
                .flatMap(b -> b ? template.expire(nk, Duration.of(
                        EXPIRATION_TIME, ChronoUnit.SECONDS)).map(bb -> nk) : Mono.empty());
    }

}
