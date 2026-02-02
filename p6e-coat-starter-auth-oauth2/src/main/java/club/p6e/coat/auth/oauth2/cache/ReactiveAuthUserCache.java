package club.p6e.coat.auth.oauth2.cache;

import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Reactive Auth User Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveAuthUserCache {

    /**
     * Auth User Cache Model
     */
    @Data
    @Accessors(chain = true)
    class Model implements Serializable {

        /**
         * UID
         */
        private String uid;

        /**
         * Token
         */
        private String token;

        /**
         * Scope
         */
        private String scope;

    }

    /**
     * User Data Cache Prefix
     */
    String USER_DATA_CACHE_PREFIX = "AUTH:OAUTH2:USER_DATA:";

    /**
     * User Token Cache Prefix
     */
    String USER_TOKEN_CACHE_PREFIX = "AUTH:OAUTH2:USER_TOKEN:";

    /**
     * Del Data
     *
     * @param token Token
     */
    Mono<String> clean(String token);

    /**
     * Get User Data
     *
     * @param uid UID
     * @return Value
     */
    Mono<String> getUser(String uid);

    /**
     * Get Token Data
     *
     * @param token Token
     * @return Model Object
     */
    Mono<Model> getToken(String token);

    /**
     * Set User Data
     *
     * @param uid        UID
     * @param token      Token
     * @param scope      Scope
     * @param content    Content
     * @param expiration Expiration Time
     */
    Mono<String> set(String uid, String token, String scope, String content, long expiration);

}
