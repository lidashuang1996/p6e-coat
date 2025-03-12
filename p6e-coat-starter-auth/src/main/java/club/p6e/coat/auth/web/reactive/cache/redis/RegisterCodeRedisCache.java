package club.p6e.coat.auth.web.reactive.cache.redis;

import club.p6e.coat.auth.web.reactive.cache.VerificationCodeRegisterCache;
import club.p6e.coat.auth.web.reactive.cache.redis.support.RedisCache;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class RegisterCodeRedisCache
        extends RedisCache implements VerificationCodeRegisterCache {

    /**
     * 内存缓存模板对象
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public RegisterCodeRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> del(String key) {
        return template.delete(CACHE_PREFIX + key);
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<List<String>> get(String key) {
        return template
                .opsForHash()
                .entries(CACHE_PREFIX + key)
                .collectList()
                .flatMap(l -> template.delete(CACHE_PREFIX + key).map(r -> l))
                .map(list -> {
                    final long now = System.currentTimeMillis();
                    final Map<String, String> map = new HashMap<>();
                    final Map<String, String> tmp = new HashMap<>(list.size());
                    list.forEach(item -> tmp.put((String) item.getKey(), (String) item.getValue()));
                    for (final String k : tmp.keySet()) {
                        try {
                            if (map.get(k) != null && now <= Long.parseLong(map.get(k))) {
                                map.put(k, map.get(k));
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                    return map;
                })
                .flatMap(m -> template
                        .delete(CACHE_PREFIX + key)
                        .flatMap(l -> template.opsForHash().putAll(CACHE_PREFIX + key, m))
                        .map(b -> new ArrayList<>(m.keySet()))
                );
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<Boolean> set(String key, String value) {
        return template
                .opsForHash()
                .put(CACHE_PREFIX + key, value,
                        String.valueOf(System.currentTimeMillis() + EXPIRATION_TIME * 1000)
                )
                .flatMap(b -> {
                    if (b) {
                        return template.expire(
                                CACHE_PREFIX + key,
                                Duration.of(EXPIRATION_TIME, ChronoUnit.SECONDS)
                        );
                    } else {
                        return Mono.just(false);
                    }
                });
    }

}
