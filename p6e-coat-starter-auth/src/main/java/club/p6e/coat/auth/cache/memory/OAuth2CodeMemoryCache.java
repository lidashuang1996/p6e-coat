package club.p6e.coat.auth.cache.memory;

import club.p6e.coat.auth.cache.OAuth2CodeCache;
import club.p6e.coat.auth.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.cache.memory.support.ReactiveMemoryTemplate;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2CodeMemoryCache
        extends MemoryCache implements OAuth2CodeCache {

    /**
     * 内存缓存模板对象
     */
    private final ReactiveMemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public OAuth2CodeMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> del(String key) {
        return Mono.just(template.del(CACHE_PREFIX + key));
    }

    @Override
    public Mono<Map<String, String>> get(String key) {
        final Map<String, String> map = get0(key);
        return map.isEmpty() ? Mono.empty() : Mono.just(map);
    }

    @Override
    public Mono<Boolean> set(String key, Map<String, String> data) {
        final Map<String, String> map = get0(key);
        map.putAll(data);
        return Mono.just(template.set(CACHE_PREFIX + key, map, EXPIRATION_TIME));
    }

    @SuppressWarnings("ALL")
    private Map<String, String> get0(String key) {
        final Map map = template.get(CACHE_PREFIX + key, Map.class);
        if (map == null) {
            return new HashMap<>();
        } else {
            return (Map<String, String>) map;
        }
    }
}
