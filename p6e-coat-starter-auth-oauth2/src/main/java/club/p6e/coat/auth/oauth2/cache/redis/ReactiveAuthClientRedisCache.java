package club.p6e.coat.auth.oauth2.cache.redis;

import club.p6e.coat.auth.oauth2.cache.ReactiveAuthClientCache;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Reactive Auth Client Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveAuthClientRedisCache.class)
@Component("club.p6e.coat.auth.oauth2.cache.redis.ReactiveAuthClientRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class ReactiveAuthClientRedisCache implements ReactiveAuthClientCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public ReactiveAuthClientRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> del(String token) {
        final String nk = CLIENT_TOKEN_CACHE_PREFIX + token;
        return template.delete(nk).map(l -> nk);
    }

    @Override
    public Mono<String> getClient(String cid) {
        return template.opsForValue().get(CLIENT_DATA_CACHE_PREFIX + cid);
    }

    @Override
    public Mono<Model> getToken(String token) {
        return template.opsForValue().get(CLIENT_TOKEN_CACHE_PREFIX + token)
                .flatMap(data -> {
                    final Model m = JsonUtil.fromJson(data, Model.class);
                    return m == null ? Mono.empty() : Mono.just(m);
                });
    }

    @Override
    public Mono<String> set(String cid, String token, String scope, String content, long expiration) {
        final String data = JsonUtil.toJson(new Model().setUid(cid).setToken(token).setScope(scope));
        return template
                .opsForValue()
                .set(CLIENT_DATA_CACHE_PREFIX + cid, content, Duration.ofSeconds(expiration))
                .flatMap(b -> template.opsForValue().set(CLIENT_TOKEN_CACHE_PREFIX + token, data, Duration.ofSeconds(expiration)))
                .map(b -> CLIENT_TOKEN_CACHE_PREFIX + token);
    }

}
