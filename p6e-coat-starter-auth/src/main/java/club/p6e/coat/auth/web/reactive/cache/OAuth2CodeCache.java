package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.ICache;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * OAUTH2 CODE 授权模式缓存
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2CodeCache extends ICache {

    /**
     * 过期的时间
     */
    long EXPIRATION_TIME = 300L;

    /**
     * OAUTH2 CODE 模式的缓存前缀
     */
    String CACHE_PREFIX = "OAUTH2:CODE:";

    /**
     * OAUTH2 作用域
     */
    String OAUTH2_SCOPE = "OAUTH2_SCOPE";

    /**
     * OAUTH2 客户端编号
     */
    String OAUTH2_CLIENT_ID = "OAUTH2_CLIENT_ID";

    /**
     * OAUTH2 重定向 URI
     */
    String OAUTH2_REDIRECT_URI = "OAUTH2_REDIRECT_URI";

    /**
     * OAUTH2 用户编号
     */
    String OAUTH2_USER_ID = "OAUTH2_USER_ID";

    /**
     * OAUTH2 用户信息
     */
    String OAUTH2_USER_INFO = "OAUTH2_USER_INFO";

    /**
     * 删除数据
     *
     * @param key 键
     * @return 删除数据的条数
     */
    Mono<Long> del(String key);

    /**
     * 读取数据
     *
     * @param key 键
     * @return 值
     */
    Mono<Map<String, String>> get(String key);

    /**
     * 写入数据
     *
     * @param key  键
     * @param data 值
     * @return 是否写入数据成功
     */
    Mono<Boolean> set(String key, Map<String, String> data);

}
