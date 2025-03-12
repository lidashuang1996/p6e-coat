package club.p6e.coat.auth.web.reactive.cache.memory;

import club.p6e.coat.auth.web.reactive.cache.OAuth2TokenClientAuthCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.web.reactive.cache.memory.support.ReactiveMemoryTemplate;
import club.p6e.coat.common.utils.JsonUtil;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2TokenClientAuthMemoryCache
        extends MemoryCache implements OAuth2TokenClientAuthCache {

    /**
     * 内存缓存模板对象
     */
    private final ReactiveMemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public OAuth2TokenClientAuthMemoryCache(ReactiveMemoryTemplate template) {
        this.template = template;
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<Token> set(String cid, String scope, String accessToken, String refreshToken, String client) {
        final Token token = new Token()
                .setCid(cid)
                .setScope(scope)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);
        final String json = JsonUtil.toJson(token);
        if (json == null) {
            return Mono.empty();
        }
        template.set(CLIENT_PREFIX + cid, client);
        template.set(ACCESS_TOKEN_PREFIX + accessToken, json, EXPIRATION_TIME);
        template.set(REFRESH_TOKEN_PREFIX + refreshToken, json, EXPIRATION_TIME);
        return Mono.just(token);
    }

    @Override
    public Mono<String> getClient(String cid) {
        final String r = template.get(CLIENT_PREFIX + cid, String.class);
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
    public Mono<Token> getAccessToken(String token) {
        return getToken(ACCESS_TOKEN_PREFIX, token);
    }

    @Override
    public Mono<Token> getRefreshToken(String token) {
        return getToken(REFRESH_TOKEN_PREFIX, token);
    }

    @SuppressWarnings("ALL")
    @Override
    public Mono<Long> cleanToken(String content) {
        final String r = template.get(ACCESS_TOKEN_PREFIX + content, String.class);
        if (r != null) {
            final Token token = JsonUtil.fromJson(r, Token.class);
            if (token != null) {
                template.del(ACCESS_TOKEN_PREFIX + token.getAccessToken());
                template.del(REFRESH_TOKEN_PREFIX + token.getRefreshToken());
                return Mono.just(1L);
            }
        }
        return Mono.just(0L);
    }

}
