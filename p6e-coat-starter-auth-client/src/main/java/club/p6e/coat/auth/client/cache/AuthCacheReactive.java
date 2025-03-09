package club.p6e.coat.auth.client.cache;

import club.p6e.coat.auth.client.cache.support.ICache;
import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * 认证缓存服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AuthCacheReactive extends ICache {

    /**
     * 令牌模型
     */
    @Data
    @Accessors(chain = true)
    class Token implements Serializable {

        /**
         * UID
         */
        private String uid;

        /**
         * DEVICE
         */
        private String device;

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
     * 分割符号
     */
    String DELIMITER = ":";

    /**
     * 过期的时间
     */
    long EXPIRATION_TIME = 3600 * 3L;

    /**
     * 用户缓存前缀
     */
    String USER_PREFIX = "AUTH:USER:";

    /**
     * ACCESS TOKEN PREFIX
     */
    String ACCESS_TOKEN_PREFIX = "AUTH:ACCESS_TOKEN:";

    /**
     * REFRESH TOKEN PREFIX
     */
    String REFRESH_TOKEN_PREFIX = "AUTH:REFRESH_TOKEN:";

    /**
     * ACCESS TOKEN PREFIX
     */
    String USER_ACCESS_TOKEN_PREFIX = "AUTH:USER:ACCESS_TOKEN:";

    /**
     * REFRESH TOKEN PREFIX
     */
    String USER_REFRESH_TOKEN_PREFIX = "AUTH:USER:REFRESH_TOKEN:";

    /**
     * 写入认证数据
     *
     * @param uid          用户数据
     * @param device       设备数据
     * @param accessToken  ACCESS TOKEN 数据
     * @param refreshToken REFRESH TOKEN 数据
     * @param user         用户信息数据
     * @return 令牌对象
     */
    Mono<Token> set(String uid, String device, String accessToken, String refreshToken, String user);

    Mono<String> getUser(String id);

    Mono<Token> getAccessToken(String token);

    Mono<Token> getRefreshToken(String token);

    Mono<Long> cleanAccessToken(String token);
}
