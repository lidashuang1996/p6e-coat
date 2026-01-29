package club.p6e.coat.auth.oauth2.cache.redis;

import club.p6e.coat.auth.oauth2.cache.BlockingAuthUserCache;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Blocking Auth User Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingAuthUserRedisCache.class)
@Component("club.p6e.coat.auth.oauth2.cache.redis.BlockingAuthUserRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingAuthUserRedisCache implements BlockingAuthUserCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public BlockingAuthUserRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void clean(String token) {
        template.delete(USER_TOKEN_CACHE_PREFIX + token);
    }

    @Override
    public String getUser(String uid) {
        return template.opsForValue().get(USER_DATA_CACHE_PREFIX + uid);
    }

    @Override
    public Model getToken(String token) {
        final String data = template.opsForValue().get(USER_TOKEN_CACHE_PREFIX + token);
        return data == null ? null : JsonUtil.fromJson(data, Model.class);
    }

    @Override
    public void set(String uid, String token, String scope, String content, long expiration) {
        final String data = JsonUtil.toJson(new Model().setUid(uid).setToken(token).setScope(scope));
        template.opsForValue().set(USER_DATA_CACHE_PREFIX + uid, content, expiration, TimeUnit.SECONDS);
        template.opsForValue().set(USER_TOKEN_CACHE_PREFIX + token, data, expiration, TimeUnit.SECONDS);
    }

}
