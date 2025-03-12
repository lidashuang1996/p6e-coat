package club.p6e.coat.auth.web.reactive.cache.memory;

import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import club.p6e.coat.common.utils.JsonUtil;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthMemoryCache extends MemoryCache implements UserTokenCache {

    /**
     * 内存缓存模板对象
     */
    private final ReactiveMemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public AuthMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Token> set(String uid, String device, String accessToken, String refreshToken, String user) {
        final Token token = new Token()
                .setUid(uid)
                .setDevice(device)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);
        final String json = JsonUtil.toJson(token);
        if (json == null) {
            return Mono.empty();
        }
        template.set(USER_PREFIX + uid, user, EXPIRATION_TIME);
        template.set(ACCESS_TOKEN_PREFIX + accessToken, json, EXPIRATION_TIME);
        template.set(REFRESH_TOKEN_PREFIX + refreshToken, json, EXPIRATION_TIME);
        template.set(USER_ACCESS_TOKEN_PREFIX + uid + DELIMITER + accessToken, json, EXPIRATION_TIME);
        template.set(USER_REFRESH_TOKEN_PREFIX + uid + DELIMITER + refreshToken, json, EXPIRATION_TIME);
        return Mono.just(token);
    }

    @Override
    public Mono<String> getUser(String uid) {
        final String r = template.get(USER_PREFIX + uid, String.class);
        return r == null ? Mono.empty() : Mono.just(r);
    }

    @SuppressWarnings("ALL")
    private Mono<Token> getToken(String prefix, String content) {
        final String r = template.get(prefix + content, String.class);
        if (r == null) {
            return Mono.empty();
        } else {
            final Token token = JsonUtil.fromJson(r, Token.class);
            return token == null ? Mono.empty() : Mono.just(token);
        }
    }

    @Override
    public Mono<Token> getAccessToken(String content) {
        return getToken(ACCESS_TOKEN_PREFIX, content);
    }

    @Override
    public Mono<Token> getRefreshToken(String content) {
        return getToken(REFRESH_TOKEN_PREFIX, content);
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<Long> cleanToken(String content) {
        return getAccessToken(content)
                .flatMap(t -> {
                    template.del(ACCESS_TOKEN_PREFIX + t.getAccessToken());
                    template.del(REFRESH_TOKEN_PREFIX + t.getRefreshToken());
                    template.del(USER_ACCESS_TOKEN_PREFIX + t.getUid() + DELIMITER + t.getAccessToken());
                    template.del(USER_REFRESH_TOKEN_PREFIX + t.getUid() + DELIMITER + t.getRefreshToken());
                    return Mono.just(1L);
                });
    }

    @Override
    public Mono<Long> cleanUserAll(String uid) {
        final String r = template.get(USER_PREFIX + uid, String.class);
        if (r != null) {
            template.names().forEach(n -> {
                if (n.startsWith(USER_ACCESS_TOKEN_PREFIX + uid + DELIMITER)
                        || n.startsWith(USER_REFRESH_TOKEN_PREFIX + uid + DELIMITER)) {
                    template.del(n);
                }
            });
        }
        template.del(USER_PREFIX + uid);
        return Mono.just(1L);
    }

}
