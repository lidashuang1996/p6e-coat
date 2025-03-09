package club.p6e.coat.auth.client.cache.memory.support;

import club.p6e.coat.auth.client.cache.support.ICache;

/**
 * Memory Cache
 *
 * @author lidashuang
 * @version 1.0
 */
public class MemoryCache implements ICache {

    @Override
    public String toType() {
        return "MEMORY_TYPE";
    }

}
