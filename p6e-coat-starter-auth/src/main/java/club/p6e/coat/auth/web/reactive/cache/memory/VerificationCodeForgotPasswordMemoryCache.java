package club.p6e.coat.auth.web.reactive.cache.memory;

import club.p6e.coat.auth.web.reactive.cache.VerificationCodeForgotPasswordCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Verification Code Forgot Password Memory Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeForgotPasswordMemoryCache extends MemoryCache implements VerificationCodeForgotPasswordCache {

    /**
     * Reactive Memory Template Object
     */
    private final ReactiveMemoryTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive Memory Template Object
     */
    public VerificationCodeForgotPasswordMemoryCache(ReactiveMemoryTemplate template) {
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
