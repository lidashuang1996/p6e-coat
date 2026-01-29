package club.p6e.coat.auth.oauth2.cache;

import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Reactive Auth Client Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveAuthClientCache {

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
     * Client Data Cache Prefix
     */
    String CLIENT_DATA_CACHE_PREFIX = "AUTH:OAUTH2:CLIENT_DATA:";

    /**
     * Client Token Cache Prefix
     */
    String CLIENT_TOKEN_CACHE_PREFIX = "AUTH:OAUTH2:CLIENT_TOKEN:";

    /**
     * Del Data
     *
     * @param token Token
     */
    Mono<String> del(String token);

    /**
     * Get Client Data
     *
     * @param cid CID
     * @return Model Object
     */
    Mono<String> getClient(String cid);

    /**
     * Get Token Data
     *
     * @param token Token
     * @return Value
     */
    Mono<Model> getToken(String token);

    /**
     * Set Client Data
     *
     * @param cid        CID
     * @param token      Token
     * @param scope      Scope
     * @param content    Content
     * @param expiration Expiration Time
     */
    Mono<String> set(String cid, String token, String scope, String content, long expiration);

}
