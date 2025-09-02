package club.p6e.coat.auth.web.cache;

/**
 * Login Quick Response Code Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LoginQuickResponseCodeCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 300L;

    /**
     * Empty Content
     */
    String EMPTY_CONTENT = "__NULL__";

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
     */
    void del(String key);

    /**
     * Get Data
     *
     * @param key Key
     * @return Value
     */
    String get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     */
    void set(String key, String value);

}
