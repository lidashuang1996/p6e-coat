package club.p6e.coat.auth.web.reactive.cache;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Verification Code Register Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeRegisterCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 180;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:REGISTER:CODE:";

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
