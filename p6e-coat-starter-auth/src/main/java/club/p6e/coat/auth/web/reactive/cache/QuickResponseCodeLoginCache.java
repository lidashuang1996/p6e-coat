package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.Cache;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QuickResponseCodeLoginCache extends Cache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 300L;

    /**
     * Empty Content
     */
    String EMPTY_CONTENT = "__null__";

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:QUICK:RESPONSE:CODE:";

    /**
     * Judgment Content Is Empty
     *
     * @param content Content
     * @return Content Is Empty Result
     */
    static boolean isEmpty(String content) {
        return EMPTY_CONTENT.equalsIgnoreCase(content);
    }

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
