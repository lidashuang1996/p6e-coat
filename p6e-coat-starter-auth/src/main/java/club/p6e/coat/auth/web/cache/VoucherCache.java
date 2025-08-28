package club.p6e.coat.auth.web.cache;

import java.util.Map;

/**
 * Voucher Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VoucherCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 900L;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:VOUCHER:";

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