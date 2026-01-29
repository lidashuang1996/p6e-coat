package club.p6e.coat.auth.oauth2.cache;

import java.util.Map;

/**
 * Blocking Code Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingCodeCache {

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
    void del(String key);

    /**
     * Get Data
     *
     * @param key Key
     * @return Value
     */
    Map<String, String> get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     */
    void set(String key, Map<String, String> value);

}
