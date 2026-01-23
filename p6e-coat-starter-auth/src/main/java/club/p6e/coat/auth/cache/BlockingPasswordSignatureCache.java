package club.p6e.coat.auth.cache;

/**
 * Blocking Password Signature Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingPasswordSignatureCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 180;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:PASSWORD:SIGNATURE_RSA:";

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
