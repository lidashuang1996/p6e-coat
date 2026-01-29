package club.p6e.coat.auth.oauth2.cache;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Reactive Code Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveCodeCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 300;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:OAUTH2:CODE:";

    /**
     * Del Data
     *
     * @param key Key
     */
    Mono<String> del(String key);

    /**
     * Get Data
     *
     * @param key Key
     * @return Value
     */
    Mono<Map<String, String>> get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     */
    Mono<String> set(String key, Map<String, String> value);

}
