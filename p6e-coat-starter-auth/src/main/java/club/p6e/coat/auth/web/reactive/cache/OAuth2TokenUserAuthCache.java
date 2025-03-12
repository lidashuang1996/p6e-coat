package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.ICache;
import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * OAuth2 用户授权令牌缓存
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2TokenUserAuthCache extends ICache {

    /**
     * 令牌模型
     */
    @Data
    @Accessors(chain = true)
    static class Token implements Serializable {

        /**
         * UID
         */
        private String uid;

        /**
         * 作用域
         */
        private String scope;

        /**
         * ACCESS TOKEN
         */
        private String accessToken;

        /**
         * REFRESH TOKEN
         */
        private String refreshToken;

    }

    /**
     * 过期的时间
     */
    long EXPIRATION_TIME = 900L;

    /**
     * 用户缓存前缀
     */
    String USER_PREFIX = "OAUTH2:USER_AUTH_TOKEN:USER:";

    /**
     * ACCESS TOKEN 缓存前缀
     */
    String ACCESS_TOKEN_PREFIX = "OAUTH2:USER_AUTH_TOKEN:ACCESS_TOKEN:";

    /**
     * REFRESH TOKEN 缓存前缀
     */
    String REFRESH_TOKEN_PREFIX = "OAUTH2:USER_AUTH_TOKEN:REFRESH_TOKEN:";

    /**
     * @param uid          用户ID
     * @param scope        用户的作用域
     * @param accessToken  Access Token
     * @param refreshToken Refresh Token
     * @param user         用户的信息
     * @return 令牌对象
     */
    Mono<Token> set(String uid, String scope, String accessToken, String refreshToken, String user);

    /**
     * 读取信息
     *
     * @param uid 用户ID
     * @return 用户信息
     */
    Mono<String> getUser(String uid);

    /**
     * 读取令牌
     *
     * @param token 令牌
     * @return 令牌对象
     */
    Mono<Token> getAccessToken(String token);

    /**
     * 读取刷新令牌
     *
     * @param token 刷新令牌
     * @return 令牌对象
     */
    Mono<Token> getRefreshToken(String token);

    /**
     * 清除令牌
     *
     * @param token 令牌
     * @return 清除的令牌条数
     */
    Mono<Long> cleanToken(String token);

}
