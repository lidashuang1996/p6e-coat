package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.Cache;
import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

/**
 * User Token Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface UserTokenCache extends Cache {

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

    /**
     * Delimiter
     */
    String DELIMITER = ":";

    /**
     * Expiration Time
     */
    long EXPIRATION_TIME = 3600 * 3L;

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
    Mono<Model> set(String uid, String device, String token, String content);

    /**
     * Get User
     *
     * @param uid UID
     * @return User Content
     */
    Mono<String> getUser(String uid);

    /**
     * Get Token
     *
     * @param token Token
     * @return Model Object
     */
    Mono<Model> getToken(String token);

    /**
     * Clean Token
     *
     * @param token Token
     * @return Model Object
     */
    Mono<Model> cleanToken(String token);

    /**
     * Clean User And User All Token
     *
     * @param uid UID
     * @return Token List Object
     */
    Mono<List<String>> cleanUserAll(String uid);

}
