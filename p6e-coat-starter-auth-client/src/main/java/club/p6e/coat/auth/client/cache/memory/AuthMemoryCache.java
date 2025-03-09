package club.p6e.coat.auth.client.cache.memory;

import club.p6e.coat.auth.client.cache.AuthCache;
import club.p6e.coat.auth.client.cache.memory.support.MemoryCache;
import club.p6e.coat.auth.client.cache.memory.support.MemoryTemplate;
import club.p6e.coat.common.utils.JsonUtil;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthMemoryCache extends MemoryCache implements AuthCache {

    /**
     * 内存缓存模板对象
     */
    private final MemoryTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 内存缓存模板对象
     */
    public AuthMemoryCache(MemoryTemplate template) {
        this.template = template;
    }

    @Override
    public Token set(String uid, String device, String accessToken, String refreshToken, String user) {
        final Token token = new Token().setUid(uid).setDevice(device)
                .setAccessToken(accessToken).setRefreshToken(refreshToken);
        final String json = JsonUtil.toJson(token);
        if (json == null) {
            return null;
        } else {
            template.set(USER_PREFIX + uid, json, EXPIRATION_TIME);
            template.set(ACCESS_TOKEN_PREFIX + accessToken, json, EXPIRATION_TIME);
            template.set(REFRESH_TOKEN_PREFIX + refreshToken, json, EXPIRATION_TIME);
            template.set(USER_ACCESS_TOKEN_PREFIX + uid + DELIMITER + accessToken, json, EXPIRATION_TIME);
            template.set(USER_REFRESH_TOKEN_PREFIX + uid + DELIMITER + refreshToken, json, EXPIRATION_TIME);
            return token;
        }
    }

    @Override
    public String getUser(String id) {
        return template.get(USER_PREFIX + id, String.class);
    }

    @Override
    public Token getAccessToken(String token) {
        final String content = template.get(ACCESS_TOKEN_PREFIX + token, String.class);
        return JsonUtil.fromJson(content, Token.class);
    }

    @Override
    public Token getRefreshToken(String token) {
        final String content = template.get(REFRESH_TOKEN_PREFIX + token, String.class);
        return JsonUtil.fromJson(content, Token.class);
    }

    @Override
    public Long cleanAccessToken(String token) {
        final Token t = getAccessToken(token);
        if (t == null) {
            return null;
        } else {
            template.del(ACCESS_TOKEN_PREFIX + t.getAccessToken());
            template.del(REFRESH_TOKEN_PREFIX + t.getRefreshToken());
            return 1L;
        }
    }

}
