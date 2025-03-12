package club.p6e.coat.auth.web.reactive.cache.redis.support;

import club.p6e.coat.auth.web.reactive.cache.support.ICache;

/**
 * Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public abstract class RedisCache implements ICache {

    @Override
    public String toType() {
        return "REDIS_TYPE";
    }

}
