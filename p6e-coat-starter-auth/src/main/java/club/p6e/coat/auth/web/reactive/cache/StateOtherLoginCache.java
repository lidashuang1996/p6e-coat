package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.ICache;
import reactor.core.publisher.Mono;

/**
 * 其他登录的 STATE 缓存
 *
 * @author lidashuang
 * @version 1.0
 */
public interface StateOtherLoginCache extends ICache {

    /**
     * 分割符号
     */
    String DELIMITER = ":";

    /**
     * 过期的时间
     */
    long EXPIRATION_TIME = 300L;

    /**
     * 缓存前缀
     */
    String CACHE_PREFIX = "LOGIN:OTHER_STATE:";

    /**
     * 删除数据
     *
     * @param type 类型
     * @param key  键
     * @return 删除数据的条数
     */
    Mono<Long> del(String type, String key);

    /**
     * 读取数据
     *
     * @param type 类型
     * @param key  键
     * @return 值
     */
    Mono<String> get(String type, String key);

    /**
     * 写入数据
     *
     * @param type  类型
     * @param key   键
     * @param value 值
     * @return 是否写入数据成功
     */
    Mono<Boolean> set(String type, String key, String value);

}
