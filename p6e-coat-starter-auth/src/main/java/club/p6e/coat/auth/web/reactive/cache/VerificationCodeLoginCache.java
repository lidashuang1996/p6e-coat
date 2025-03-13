package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.Cache;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 验证码登录的验证码缓存
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeLoginCache extends Cache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 200L;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:VERIFICATION:CODE:";

    /**
     * Del Data
     *
     * @param key Key
     * @return Cache Key
     */
    Mono<String> del(String key);

    /**
     * 读取数据
     *
     * @param key 键
     * @return 读取的列表数据
     */
    Mono<List<String>> get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     * @return Cache Key
     */
    Mono<String> set(String key, String value);

}
