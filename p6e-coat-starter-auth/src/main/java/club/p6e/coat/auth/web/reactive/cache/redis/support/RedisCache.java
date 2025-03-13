package club.p6e.coat.auth.web.reactive.cache.redis.support;

import club.p6e.coat.auth.web.reactive.cache.support.Cache;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public abstract class RedisCache implements Cache {

    @Override
    public String type() {
        return "REDIS_TYPE";
    }

    public Mono<String> delVerificationCode(ReactiveStringRedisTemplate template, String key) {
        return template.delete(key).map(l -> key);
    }

    public Mono<List<String>> getVerificationCode(ReactiveStringRedisTemplate template, String key, long expiration) {
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
                        .delete(key)
                        .flatMap(l -> template.opsForHash().putAll(key, m))
                        .flatMap(b -> template.expire(key, Duration.of(expiration, ChronoUnit.SECONDS)))
                        .map(b -> new ArrayList<>(m.keySet()))
                );
    }

    public Mono<String> setVerificationCode(ReactiveStringRedisTemplate template, String key, String value, long expiration) {
        final String result = key + "@" + value;
        final String timestamp = String.valueOf(System.currentTimeMillis() + expiration * 1000L);
        return template.opsForHash().put(key, value, timestamp)
                .flatMap(b -> b ? template.expire(key,
                        Duration.of(expiration, ChronoUnit.SECONDS)).map(bb -> result) : Mono.empty());
    }

}
