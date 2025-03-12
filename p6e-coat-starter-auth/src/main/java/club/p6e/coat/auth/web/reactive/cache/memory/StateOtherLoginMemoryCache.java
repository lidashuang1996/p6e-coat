package club.p6e.coat.auth.web.reactive.cache.memory;

import club.p6e.coat.auth.web.reactive.cache.StateOtherLoginCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class StateOtherLoginMemoryCache
        extends MemoryCache implements StateOtherLoginCache {

    /**
     * 内存缓存模板对象
     */
    private final ReactiveMemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public StateOtherLoginMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> del(String type, String key) {
        return Mono.just(template.del(CACHE_PREFIX + type + DELIMITER + key));
    }

    @Override
    public Mono<String> get(String type, String key) {
        final String r = template.get(CACHE_PREFIX + type + DELIMITER + key, String.class);
        return r == null ? Mono.empty() : Mono.just(r);
    }

    @Override
    public Mono<Boolean> set(String type, String key, String value) {
        return Mono.just(template.set(CACHE_PREFIX + type + DELIMITER + key, value, EXPIRATION_TIME));
    }

}
