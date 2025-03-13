package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.Cache;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Verification Code Forgot Password Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeForgotPasswordCache extends Cache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 180;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:FORGOT_PASSWORD:CODE:";

    /**
     * Del Data
     *
     * @param key Key
     * @return Cache Key
     */
    Mono<String> del(String key);

    /**
     * Get Data
     *
     * @param key Key
     * @return Value
     */
    Mono<List<String>> get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     * @return Cache Key
     */
    Mono<String> set(String key, String value);

}
