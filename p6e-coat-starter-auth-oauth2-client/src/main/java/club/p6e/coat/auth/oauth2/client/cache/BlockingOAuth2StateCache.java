package club.p6e.coat.auth.oauth2.client.cache;

/**
 * Blocking OAuth2 State Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingOAuth2StateCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 300L;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "OAUTH2:AUTH:STATE:";

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
