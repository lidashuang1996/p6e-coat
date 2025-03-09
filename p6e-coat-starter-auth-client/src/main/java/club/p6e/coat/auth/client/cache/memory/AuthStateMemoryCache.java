package club.p6e.coat.auth.client.cache.memory;

import club.p6e.coat.auth.client.cache.AuthStateCache;
import club.p6e.coat.auth.client.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.client.cache.memory.support.MemoryTemplate;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthStateMemoryCache extends MemoryCache implements AuthStateCache {

    /**
     * 内存缓存模板对象
     */
    private final MemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public AuthStateMemoryCache(MemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Boolean del(String key) {
        return template.del(CACHE_PREFIX + key) > 0;
    }

    @Override
    public String get(String key) {
        return template.get(CACHE_PREFIX + key, String.class);
    }

    @Override
    public Boolean set(String key, String value) {
        return template.set(CACHE_PREFIX + key, value, EXPIRATION_TIME);
    }

    @Override
    public String getAndDel(String key) {
        final String value = get(key);
        if (value != null) {
            del(value);
        }
        return value;
    }
}
