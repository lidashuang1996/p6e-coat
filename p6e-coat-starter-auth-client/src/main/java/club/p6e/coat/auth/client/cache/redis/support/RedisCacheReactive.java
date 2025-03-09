package club.p6e.coat.auth.client.cache.redis.support;

import club.p6e.coat.auth.client.cache.support.ICache;

/**
 * @author lidashuang
 * @version 1.0
 */
public class RedisCacheReactive implements ICache {

    @Override
    public String toType() {
        return "REDIS_TYPE";
    }

}
