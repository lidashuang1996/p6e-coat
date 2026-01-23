package club.p6e.coat.auth.cache;

import java.util.List;

/**
 * Blocking Login Verification Code Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingLoginVerificationCodeCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 200L;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:VERIFICATION:CODE:";

    /**
     * Del Data
     *
     * @param key Key
     */
    void del(String key);

    /**
     * 读取数据
     *
     * @param key 键
     * @return 读取的列表数据
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
