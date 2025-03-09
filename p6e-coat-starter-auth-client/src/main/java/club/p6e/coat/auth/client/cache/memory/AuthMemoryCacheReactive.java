package club.p6e.coat.auth.client.cache.memory;

import club.p6e.coat.auth.client.cache.AuthCacheReactive;
import club.p6e.coat.auth.client.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.client.cache.memory.support.MemoryTemplate;
import club.p6e.coat.common.utils.JsonUtil;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthMemoryCacheReactive extends MemoryCache implements AuthCacheReactive {

    /**
     * 内存缓存模板对象
     */
    private final MemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public AuthMemoryCacheReactive(MemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Token> set(String uid, String device, String accessToken, String refreshToken, String user) {
        final Token token = new Token().setUid(uid).setDevice(device)
                .setAccessToken(accessToken).setRefreshToken(refreshToken);
        final String json = JsonUtil.toJson(token);
        if (json == null) {
            return Mono.empty();
        } else {
            template.set(REFRESH_TOKEN_PREFIX + refreshToken, json, EXPIRATION_TIME);
            template.set(ACCESS_TOKEN_PREFIX + accessToken, json, EXPIRATION_TIME);
            template.set(USER_REFRESH_TOKEN_PREFIX + uid + DELIMITER + refreshToken, json, EXPIRATION_TIME);
            template.set(USER_ACCESS_TOKEN_PREFIX + uid + DELIMITER + accessToken, json, EXPIRATION_TIME);
            template.set(USER_PREFIX + uid, json, EXPIRATION_TIME);
            return Mono.just(token);
        }
    }

    @Override
    public Mono<String> getUser(String id) {
        return Mono.just(template.get(USER_PREFIX + id, String.class));
    }

    @Override
    public Mono<Token> getAccessToken(String token) {
        final String content = template.get(ACCESS_TOKEN_PREFIX + token, String.class);
        final Token t = JsonUtil.fromJson(content, Token.class);
        return t == null ? Mono.empty() : Mono.just(t);
    }

    @Override
    public Mono<Token> getRefreshToken(String token) {
        final String content = template.get(REFRESH_TOKEN_PREFIX + token, String.class);
        final Token t = JsonUtil.fromJson(content, Token.class);
        return t == null ? Mono.empty() : Mono.just(t);
    }

    @Override
    public Mono<Long> cleanAccessToken(String token) {
        return getAccessToken(token)
                .map(t -> {
                    template.del(ACCESS_TOKEN_PREFIX + t.getAccessToken());
                    template.del(REFRESH_TOKEN_PREFIX + t.getRefreshToken());
                    return 1L;
                });
    }

}
