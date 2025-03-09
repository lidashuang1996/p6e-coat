package club.p6e.coat.auth.cache.redis;

import club.p6e.coat.auth.cache.StateOtherLoginCache;
import club.p6e.coat.auth.cache.redis.support.RedisCache;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author lidashuang
 * @version 1.0
 */
public class StateOtherLoginRedisCache
        extends RedisCache implements StateOtherLoginCache {

    /**
     * 缓存对象
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 缓存对象
     */
    public StateOtherLoginRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> del(String type, String key) {
        return template.delete(CACHE_PREFIX + type + DELIMITER + key);
    }

    @Override
    public Mono<String> get(String type, String key) {
        return template.opsForValue().get(CACHE_PREFIX + type + DELIMITER + key);
    }

    @Override
    public Mono<Boolean> set(String type, String key, String value) {
        return template.opsForValue().set(CACHE_PREFIX + type + DELIMITER + key, value, Duration.ofSeconds(EXPIRATION_TIME));
    }

}
