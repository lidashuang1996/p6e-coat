package club.p6e.coat.auth.cache.memory;

import club.p6e.coat.auth.cache.ForgotPasswordCodeCache;
import club.p6e.coat.auth.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.cache.memory.support.ReactiveMemoryTemplate;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class ForgotPasswordCodeMemoryCache
        extends MemoryCache implements ForgotPasswordCodeCache {

    /**
     * 内存缓存模板对象
     */
    private final ReactiveMemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public ForgotPasswordCodeMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> del(String key) {
        return Mono.just(template.del(CACHE_PREFIX + key));
    }

    @Override
    public Mono<List<String>> get(String key) {
        final Map<String, String> data = get0(key);
        template.set(CACHE_PREFIX + key, data, EXPIRATION_TIME);
        return data.isEmpty() ? Mono.empty() : Mono.just(new ArrayList<>(data.keySet()));
    }

    @Override
    public Mono<Boolean> set(String key, String value) {
        final Map<String, String> data = get0(key);
        data.put(value, String.valueOf(System.currentTimeMillis() + EXPIRATION_TIME * 1000));
        return Mono.just(template.set(CACHE_PREFIX + key, data, EXPIRATION_TIME));
    }

    @SuppressWarnings("ALL")
    public Map<String, String> get0(String key) {
        final Map map = template.get(CACHE_PREFIX + key, Map.class);
        if (map == null) {
            return new HashMap<>();
        } else {
            final long now = System.currentTimeMillis();
            final Map<String, String> result = (Map<String, String>) map;
            for (final String k : result.keySet()) {
                try {
                    if (result.get(k) == null || now > Long.parseLong(result.get(k))) {
                        result.remove(k);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            return result;
        }
    }
}
