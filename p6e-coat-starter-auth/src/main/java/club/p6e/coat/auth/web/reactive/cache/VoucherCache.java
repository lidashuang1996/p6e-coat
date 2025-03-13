package club.p6e.coat.auth.web.reactive.cache;

import club.p6e.coat.auth.web.reactive.cache.support.Cache;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Voucher Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VoucherCache extends Cache {

    /**
     * Cache Expiration Time
     */
    long EXPIRATION_TIME = 900L;

    /**
     * Cache Prefix
     */
    String CACHE_PREFIX = "AUTH:VOUCHER:";

    /**
     * Del Data
     *
     * @param key Key
     * @return Value
     */
    Mono<String> del(String key);

    /**
     * 读取数据
     *
     * @param key 键
     * @return 值
     */
    Mono<Map<String, String>> get(String key);

    /**
     * Set Data
     *
     * @param key   Key
     * @param value Value
     * @return Cache Key
     */
    Mono<String> set(String key, Map<String, String> value);

}