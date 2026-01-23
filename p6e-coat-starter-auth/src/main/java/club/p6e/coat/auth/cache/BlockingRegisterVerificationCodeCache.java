package club.p6e.coat.auth.cache;

import java.util.List;

/**
 * Blocking Register Verification Code Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingRegisterVerificationCodeCache {

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
     */
    void del(String key);

    /**
     * Get Data
     *
     * @param key Key
     * @return Value
     */
    List<String> get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     */
    void set(String key, String value);

}
