package club.p6e.coat.auth.web.reactive.cache.memory;

import club.p6e.coat.auth.web.reactive.cache.VoucherCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Voucher Memory Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class VoucherMemoryCache extends MemoryCache implements VoucherCache {

    /**
     * Reactive Memory Template Object
     */
    private final ReactiveMemoryTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive Memory Template Object
     */
    public VoucherMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String key) {
        final String nk = CACHE_PREFIX + key;
        return Mono.just(template.del(nk)).map(l -> key);
    }

    @Override
    public Mono<Map<String, String>> get(String key) {
        final String nk = CACHE_PREFIX + key;
        final Map<String, String> map = getData(template, nk);
        return map.isEmpty() ? Mono.empty() : Mono.just(map);
    }

    @Override
    public Mono<String> set(String key, Map<String, String> data) {
        final String nk = CACHE_PREFIX + key;
        final Map<String, String> map = getData(template, nk);
        map.putAll(data);
        return Mono.just(template.set(nk, map, EXPIRATION_TIME)).map(b -> key);
    }

}
