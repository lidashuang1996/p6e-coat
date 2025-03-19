package club.p6e.cloud.gateway.auth;

import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Auth Gateway Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AuthenticationGatewayCache {

    /**
     * Token Model
     */
    @Data
    @Accessors(chain = true)
    class Token implements Serializable {

        /**
         * UID
         */
        private String uid;

        /**
         * TOKEN
         */
        private String token;

    }

    /**
     * User Cache Prefix
     */
    String USER_PREFIX = "AUTH:USER:";

    /**
     * Token Cache Prefix
     */
    String TOKEN_PREFIX = "AUTH:TOKEN:";

    /**
     * User Token Cache Prefix
     */
    String USER_TOKEN_PREFIX = "AUTH:USER:TOKEN:";

    /**
     * Get User Content
     *
     * @param uid User ID
     * @return User Content
     */
    Mono<String> getUser(String uid);

    /**
     * Get Token Object
     *
     * @param token Token
     * @return Token Object
     */
    Mono<Token> getToken(String token);

}
