package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.token.web.UserTokenCache;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User Token Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class UserTokenRedisCache implements UserTokenCache {

    /**
     * String Redis Template Object
     */
    private final StringRedisTemplate template;

    /**
     * Constructor Initialization
     *
     * @param template String Redis Template Object
     */
    public UserTokenRedisCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Model set(String uid, String device, String token, String content, long expiration) {
        final Model model = new Model().setUid(uid).setDevice(device).setToken(token);
        final String json = JsonUtil.toJson(model);
        if (json == null) {
            return null;
        }
        final byte[] jBytes = json.getBytes(StandardCharsets.UTF_8);
        return template.execute((RedisCallback<Model>) connection -> {
            connection.stringCommands().set(
                    (USER_CACHE_PREFIX + uid).getBytes(StandardCharsets.UTF_8),
                    content.getBytes(StandardCharsets.UTF_8),
                    Expiration.from(expiration, TimeUnit.SECONDS),
                    RedisStringCommands.SetOption.UPSERT
            );
            connection.stringCommands().set(
                    (TOKEN_CACHE_PREFIX + token).getBytes(StandardCharsets.UTF_8),
                    jBytes,
                    Expiration.from(expiration, TimeUnit.SECONDS),
                    RedisStringCommands.SetOption.UPSERT
            );
            connection.stringCommands().set(
                    (USER_TOKEN_CACHE_PREFIX + uid + DELIMITER + token).getBytes(StandardCharsets.UTF_8),
                    jBytes,
                    Expiration.from(expiration, TimeUnit.SECONDS),
                    RedisStringCommands.SetOption.UPSERT
            );
            return model;
        });
    }

    @Override
    public String getUser(String uid) {
        return template.opsForValue().get(USER_CACHE_PREFIX + uid);
    }

    @Override
    public Model getToken(String token) {
        return JsonUtil.fromJson(template.opsForValue().get(TOKEN_CACHE_PREFIX + token), Model.class);
    }

    @Override
    public Model cleanToken(String content) {
        final Model model = getToken(content);
        template.delete(TOKEN_CACHE_PREFIX + model.getToken());
        template.delete(USER_TOKEN_CACHE_PREFIX + model.getUid() + DELIMITER + model.getToken());
        return model;
    }

    @Override
    public List<String> cleanUserAll(String uid) {
        final List<String> result = new ArrayList<>();
        try (Cursor<String> cursor = template.scan(
                ScanOptions.scanOptions().match(USER_TOKEN_CACHE_PREFIX + uid + DELIMITER + "*").build()
        )) {
            while (cursor.hasNext()) {
                final String token1 = cursor.next();
                final String token2 = TOKEN_CACHE_PREFIX + token1.substring(token1.lastIndexOf(DELIMITER) + 1);
                result.add(token1);
                result.add(token2);
                template.delete(token1);
                template.delete(token2);
            }
            result.add(USER_CACHE_PREFIX + uid);
            template.delete(USER_CACHE_PREFIX + uid);
        } catch (Exception e) {
            // ignore exception
        }
        return result;
    }

}
