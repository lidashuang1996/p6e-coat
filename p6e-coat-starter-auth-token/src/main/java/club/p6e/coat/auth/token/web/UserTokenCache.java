package club.p6e.coat.auth.token.web;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * User Token Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public interface UserTokenCache {

    /**
     * Delimiter
     */
    String DELIMITER = ":";

    /**
     * User Cache Prefix
     */
    String USER_CACHE_PREFIX = "AUTH:USER:";

    /**
     * Token Cache Prefix
     */
    String TOKEN_CACHE_PREFIX = "AUTH:TOKEN:";

    /**
     * User Token Cache Prefix
     */
    String USER_TOKEN_CACHE_PREFIX = "AUTH:USER_TOKEN:";

    /**
     * Set Model
     *
     * @param uid     UID
     * @param device  Device
     * @param token   Token
     * @param content User Content
     * @return Model Object
     */
    Model set(String uid, String device, String token, String content, long expiration);

    /**
     * Get User
     *
     * @param uid UID
     * @return User Content
     */
    String getUser(String uid);

    /**
     * Get Token
     *
     * @param token Token
     * @return Model Object
     */
    Model getToken(String token);

    /**
     * Clean Token
     *
     * @param token Token
     * @return Model Object
     */
    Model cleanToken(String token);

    /**
     * Clean User And User All Token
     *
     * @param uid UID
     * @return Token List Object
     */
    List<String> cleanUserAll(String uid);

    /**
     * Model
     */
    @Data
    @Accessors(chain = true)
    class Model implements Serializable {

        /**
         * UID
         */
        private String uid;

        /**
         * Device
         */
        private String device;

        /**
         * TOKEN
         */
        private String token;

    }

}
