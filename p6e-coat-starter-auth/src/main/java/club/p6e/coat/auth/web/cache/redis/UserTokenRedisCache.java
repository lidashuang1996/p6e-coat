package club.p6e.coat.auth.web.cache.redis;

import club.p6e.coat.auth.token.web.UserTokenCache;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Custom User Token Redis Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(UserTokenRedisCache.class)
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
            return Mono.empty();
        }
        final byte[] jBytes = json.getBytes(StandardCharsets.UTF_8);
        return template.execute(connection -> Flux.concat(connection.stringCommands().set(
                        ByteBuffer.wrap((USER_CACHE_PREFIX + uid).getBytes(StandardCharsets.UTF_8)),
                        ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8)),
                        Expiration.from(expiration, TimeUnit.SECONDS),
                        RedisStringCommands.SetOption.UPSERT
                ),
                connection.stringCommands().set(
                        ByteBuffer.wrap((TOKEN_CACHE_PREFIX + token).getBytes(StandardCharsets.UTF_8)),
                        ByteBuffer.wrap(jBytes),
                        Expiration.from(expiration, TimeUnit.SECONDS),
                        RedisStringCommands.SetOption.UPSERT
                ),
                connection.stringCommands().set(
                        ByteBuffer.wrap((USER_TOKEN_CACHE_PREFIX + uid + DELIMITER + token).getBytes(StandardCharsets.UTF_8)),
                        ByteBuffer.wrap(jBytes),
                        Expiration.from(expiration, TimeUnit.SECONDS),
                        RedisStringCommands.SetOption.UPSERT
                ))).count().map(l -> model);
    }

    @Override
    public String getUser(String uid) {
        return template.opsForValue().get(ByteBuffer.wrap((USER_CACHE_PREFIX + uid).getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public Model getToken(String token) {
        return template
                .opsForValue()
                .get(TOKEN_CACHE_PREFIX + token)
                .flatMap(s -> {
                    final Model model = JsonUtil.fromJson(s, Model.class);
                    return model == null ? Mono.empty() : Mono.just(model);
                });
    }

    @Override
    public Model cleanToken(String content) {
        return getToken(content).flatMap(m -> template.delete(
                TOKEN_CACHE_PREFIX + m.getToken(),
                USER_TOKEN_CACHE_PREFIX + m.getUid() + DELIMITER + m.getToken()
        ).map(l -> m));
    }

    @Override
    public List<String> cleanUserAll(String uid) {
        return getUser(uid).flatMap(s -> Flux.concat(template.scan(
                        ScanOptions.scanOptions().match(TOKEN_CACHE_PREFIX + uid + DELIMITER + "*").build()
                )).collectList())
                .flatMap(l -> template.delete(l.toArray(new String[0])).map(ll -> l))
                .flatMap(l -> template.delete(USER_CACHE_PREFIX + uid).map(ll -> l));
    }

}
