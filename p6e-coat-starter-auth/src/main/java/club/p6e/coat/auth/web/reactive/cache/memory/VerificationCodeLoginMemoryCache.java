package club.p6e.coat.auth.web.reactive.cache.memory;

import club.p6e.coat.auth.web.reactive.cache.VerificationCodeLoginCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Verification Code Login Memory Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeLoginMemoryCache extends MemoryCache implements VerificationCodeLoginCache {

    /**
     * Reactive Memory Template Object
     */
    private final ReactiveMemoryTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive Memory Template Object
     */
    public VerificationCodeLoginMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String key) {
        return delVerificationCode(template, key);
    }

    @Override
    public Mono<List<String>> get(String key) {
        return getVerificationCode(template, CACHE_PREFIX + key, EXPIRATION_TIME);
    }

    @Override
    public Mono<String> set(String key, String value) {
        return setVerificationCode(template, CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

}
