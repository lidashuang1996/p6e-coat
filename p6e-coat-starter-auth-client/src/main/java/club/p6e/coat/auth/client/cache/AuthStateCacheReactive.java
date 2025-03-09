package club.p6e.coat.auth.client.cache;

import club.p6e.coat.auth.client.cache.support.ICache;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface AuthStateCacheReactive extends ICache {

    /**
     * 过期的时间
     */
    long EXPIRATION_TIME = 300L;

    /**
     * 缓存前缀
     */
    String CACHE_PREFIX = "AUTH:STATE:";

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
    Mono<String> get(String key);

    /**
     * 写入数据
     *
     * @param key   键
     * @param value 值
     * @return 是否写入数据成功
     */
    Mono<Boolean> set(String key, String value);

    /**
     * 读取数据然后删除数据
     *
     * @param key 键
     * @return 值
     */
    Mono<String> getAndDel(String key);

}
