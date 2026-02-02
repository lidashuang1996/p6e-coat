package club.p6e.coat.auth.oauth2.cache.redis;

import club.p6e.coat.auth.oauth2.cache.BlockingAuthClientCache;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Blocking Auth Client Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingAuthClientRedisCache.class)
@Component("club.p6e.coat.auth.oauth2.cache.redis.BlockingAuthClientRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingAuthClientRedisCache implements BlockingAuthClientCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public BlockingAuthClientRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void del(String token) {
        template.delete(CLIENT_TOKEN_CACHE_PREFIX + token);
    }

    @Override
    public String getClient(String uid) {
        return template.opsForValue().get(CLIENT_DATA_CACHE_PREFIX + uid);
    }

    @Override
    public Model getToken(String token) {
        final String data = template.opsForValue().get(CLIENT_TOKEN_CACHE_PREFIX + token);
        return data == null ? null : JsonUtil.fromJson(data, Model.class);
    }

    @Override
    public void set(String cid, String token, String scope, String content, long expiration) {
        final String data = JsonUtil.toJson(new Model().setCid(cid).setToken(token).setScope(scope));
        template.opsForValue().set(CLIENT_DATA_CACHE_PREFIX + cid, content, expiration, TimeUnit.SECONDS);
        template.opsForValue().set(CLIENT_TOKEN_CACHE_PREFIX + token, data, expiration, TimeUnit.SECONDS);
    }

}
