package club.p6e.coat.auth.cache;

import club.p6e.coat.auth.cache.support.ICache;
import reactor.core.publisher.Mono;

/**
 * 账号密码登录对密码签名的缓存
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PasswordSignatureCache extends ICache {

    /**
     * 过期的时间
     */
    long EXPIRATION_TIME = 180;

    /**
     * 用户缓存前缀
     */
    String CACHE_PREFIX = "PASSWORD:SIGNATURE_RSA:";

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

}
