package club.p6e.coat.auth.cache;

import reactor.core.publisher.Mono;

/**
 * Reactive Password Signature Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactivePasswordSignatureCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 180;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:PASSWORD:SIGNATURE:RSA:";

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
    Mono<String> get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     * @return Cache Key
     */
    Mono<String> set(String key, String value);

}
