package club.p6e.coat.auth.oauth2.cache;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Blocking Auth Client Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingAuthClientCache {

    /**
     * Auth Client Cache Model
     */
    @Data
    @Accessors(chain = true)
    class Model implements Serializable {

        /**
         * CID
         */
        private String cid;

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
    void del(String token);

    /**
     * Get Client Data
     *
     * @param cid CID
     * @return Value
     */
    String getClient(String cid);

    /**
     * Get Token Data
     *
     * @param token Token
     * @return Model Object
     */
    Model getToken(String token);

    /**
     * Set Client Data
     *
     * @param cid        CID
     * @param token      Token
     * @param scope      Scope
     * @param content    Content
     * @param expiration Expiration Time
     */
    void set(String cid, String token, String scope, String content, long expiration);

}
