package club.p6e.coat.auth.cache;

import club.p6e.coat.auth.cache.support.ICache;
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
public interface UserTokenCache extends ICache {

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
         * 设备
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
     * ACCESS TOKEN 缓存前缀
     */
    String ACCESS_TOKEN_PREFIX = "AUTH:ACCESS_TOKEN:";

    /**
     * REFRESH TOKEN 缓存前缀
     */
    String REFRESH_TOKEN_PREFIX = "AUTH:REFRESH_TOKEN:";

    /**
     * ACCESS TOKEN 缓存前缀
     */
    String USER_ACCESS_TOKEN_PREFIX = "AUTH:USER:ACCESS_TOKEN:";

    /**
     * REFRESH TOKEN 缓存前缀
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
    Mono<Token> set(String uid, String device, String token, String user);

    /**
     * 读取用户内容
     *
     * @param uid 令牌内容
     * @return Mono/String 读取用户内容
     */
    Mono<String> getUser(String uid);

    /**
     * 读取 ACCESS TOKEN 令牌内容
     *
     * @param content 令牌内容
     * @return Mono/Token 读取 ACCESS TOKEN 令牌内容
     */
    Mono<Token> getToken(String content);

    /**
     * 读取 REFRESH TOKEN 令牌内容
     *
     * @param content 令牌内容
     * @return Mono/Token 读取 REFRESH TOKEN 令牌内容
     */
    Mono<Token> getRefreshToken(String content);

    /**
     * 清除用户的 ACCESS TOKEN 令牌对应的信息
     *
     * @param content 令牌内容
     * @return Mono/Long 清除的数据条数
     */
    Mono<Long> cleanToken(String content);

    /**
     * 清除用户的全部信息
     *
     * @param uid 用户 ID
     * @return Mono/Long 清除的数据条数
     */
    Mono<Long> cleanUserAll(String uid);

}
