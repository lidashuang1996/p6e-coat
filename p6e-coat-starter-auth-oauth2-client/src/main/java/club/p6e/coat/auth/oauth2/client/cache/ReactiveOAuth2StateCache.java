package club.p6e.coat.auth.oauth2.client.cache;

import reactor.core.publisher.Mono;

/**
 * Reactive OAuth2 State Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveOAuth2StateCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 300L;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "OAUTH2:STATE:";

    /**
     * Del Data
     *
     * @param key Key
     * @return Cache Key
     */
    Mono<Long> del(String key);

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
    Mono<Boolean> set(String key, String value);

}
