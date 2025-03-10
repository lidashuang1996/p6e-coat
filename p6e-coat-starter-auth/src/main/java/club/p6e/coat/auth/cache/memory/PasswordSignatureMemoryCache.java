package club.p6e.coat.auth.cache.memory;

import club.p6e.coat.auth.cache.AccountPasswordLoginSignatureCache;
import club.p6e.coat.auth.cache.memory.support.ReactiveMemoryTemplate;
import club.p6e.coat.auth.cache.memory.support.MemoryCache;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class PasswordSignatureMemoryCache
        extends MemoryCache implements AccountPasswordLoginSignatureCache {

    /**
     * 内存缓存模板对象
     */
    private final ReactiveMemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public PasswordSignatureMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> del(String key) {
        return Mono.just(template.del(CACHE_PREFIX + key));
    }

    @Override
    public Mono<String> get(String key) {
        final String r = template.get(CACHE_PREFIX + key, String.class);
        return r == null ? Mono.empty() : Mono.just(r);
    }

    @Override
    public Mono<Boolean> set(String key, String value) {
        return Mono.just(template.set(CACHE_PREFIX + key, value, EXPIRATION_TIME));
    }

}
