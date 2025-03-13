package club.p6e.coat.auth.web.reactive.cache.memory;

import club.p6e.coat.auth.web.reactive.cache.QuickResponseCodeLoginCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Memory Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class QuickResponseCodeLoginMemoryCache extends MemoryCache implements QuickResponseCodeLoginCache {

    /**
     * Reactive Memory Template Object
     */
    private final ReactiveMemoryTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive Memory Template Object
     */
    public QuickResponseCodeLoginMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String key) {
        final String nk = CACHE_PREFIX + key;
        return Mono.just(template.del(nk)).map(l -> nk);
    }

    @Override
    public Mono<String> get(String key) {
        final String r = template.get(CACHE_PREFIX + key, String.class);
        return r == null ? Mono.empty() : Mono.just(r);
    }

    @Override
    public Mono<String> set(String key, String value) {
        final String nk = CACHE_PREFIX + key;
        return Mono.just(template.set(nk, value, EXPIRATION_TIME)).map(b -> nk);
    }

}
