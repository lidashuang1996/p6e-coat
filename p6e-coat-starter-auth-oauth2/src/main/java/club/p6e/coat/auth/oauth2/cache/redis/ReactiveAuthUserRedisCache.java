package club.p6e.coat.auth.oauth2.cache.redis;

import club.p6e.coat.auth.oauth2.cache.ReactiveAuthClientCache;
import club.p6e.coat.auth.oauth2.cache.ReactiveAuthUserCache;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Reactive Auth User Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveAuthUserRedisCache.class)
@Component("club.p6e.coat.auth.oauth2.cache.redis.ReactiveAuthUserRedisCache")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class ReactiveAuthUserRedisCache implements ReactiveAuthUserCache {

    /**
     * Reactive String Redis Template Object
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template Reactive String Redis Template Object
     */
    public ReactiveAuthUserRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<String> clean(String token) {
        final String nk = USER_TOKEN_CACHE_PREFIX + token;
        return template.delete(nk).map(l -> nk);
    }

    @Override
    public Mono<String> getUser(String uid) {
        return template.opsForValue().get(USER_DATA_CACHE_PREFIX + uid);
    }

    @Override
    public Mono<Model> getToken(String token) {
        return template.opsForValue().get(USER_TOKEN_CACHE_PREFIX + token)
                .flatMap(data -> {
                    final Model m = JsonUtil.fromJson(data, Model.class);
                    return m == null ? Mono.empty() : Mono.just(m);
                });
    }

    @Override
    public Mono<String> set(String uid, String token, String scope, String content, long expiration) {
        final String data = JsonUtil.toJson(new ReactiveAuthClientCache.Model().setUid(uid).setToken(token).setScope(scope));
        return template
                .opsForValue()
                .set(USER_DATA_CACHE_PREFIX + uid, content, Duration.ofSeconds(expiration))
                .flatMap(b -> template.opsForValue().set(USER_TOKEN_CACHE_PREFIX + token, data, Duration.ofSeconds(expiration)))
                .map(b -> USER_TOKEN_CACHE_PREFIX + token);
    }

}
