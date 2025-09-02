package club.p6e.coat.auth.web.cache;

import java.util.List;

/**
 * Forgot Password Verification Code Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ForgotPasswordVerificationCodeCache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 180;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:FORGOT_PASSWORD:CODE:";

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
