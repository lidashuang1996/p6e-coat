package club.p6e.coat.auth.client.cache.memory;

import club.p6e.coat.auth.client.cache.AuthStateCacheReactive;
import club.p6e.coat.auth.client.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.client.cache.memory.support.MemoryTemplate;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthStateMemoryCacheReactive extends MemoryCache implements AuthStateCacheReactive {

    /**
     * 内存缓存模板对象
     */
    private final MemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public AuthStateMemoryCacheReactive(MemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> del(String key) {
        return Mono.just(template.del(key));
    }

    @Override
    public Mono<String> get(String key) {
        final String value = template.get(key, String.class);
        return value == null ? Mono.empty() : Mono.just(value);
    }

    @Override
    public Mono<Boolean> set(String key, String value) {
        return Mono.just(template.set(key, value, EXPIRATION_TIME));
    }

    @Override
    public Mono<String> getAndDel(String key) {
        return get(key).flatMap(v -> del(key).map(l -> v));
    }
}
