package club.p6e.coat.auth.cache.redis;

import club.p6e.coat.auth.cache.redis.support.RedisCache;
import club.p6e.coat.auth.cache.UserTokenCache;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthRedisCache
        extends RedisCache implements UserTokenCache {

    /**
     * 缓存对象
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 缓存对象
     */
    public AuthRedisCache(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    @SuppressWarnings("ALL")
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
        long expirationTime = EXPIRATION_TIME;
        if ("ANDROID".equalsIgnoreCase(device)) {
            expirationTime = 3600 * 24 * 15L;
        }
        final long finalExpirationTime = expirationTime;
        final byte[] jcBytes = json.getBytes(StandardCharsets.UTF_8);
        return template.execute(connection ->
                Flux.concat(
                        connection.stringCommands().set(
                                ByteBuffer.wrap((USER_PREFIX + uid).getBytes(StandardCharsets.UTF_8)),
                                ByteBuffer.wrap(user.getBytes(StandardCharsets.UTF_8)),
                                Expiration.from(finalExpirationTime, TimeUnit.SECONDS),
                                RedisStringCommands.SetOption.UPSERT
                        ),
                        connection.stringCommands().set(
                                ByteBuffer.wrap((ACCESS_TOKEN_PREFIX + accessToken).getBytes(StandardCharsets.UTF_8)),
                                ByteBuffer.wrap(jcBytes),
                                Expiration.from(finalExpirationTime, TimeUnit.SECONDS),
                                RedisStringCommands.SetOption.UPSERT
                        ),
                        connection.stringCommands().set(
                                ByteBuffer.wrap((REFRESH_TOKEN_PREFIX + refreshToken).getBytes(StandardCharsets.UTF_8)),
                                ByteBuffer.wrap(jcBytes),
                                Expiration.from(finalExpirationTime, TimeUnit.SECONDS),
                                RedisStringCommands.SetOption.UPSERT
                        ),
                        connection.stringCommands().set(
                                ByteBuffer.wrap((USER_ACCESS_TOKEN_PREFIX + uid + DELIMITER + accessToken).getBytes(StandardCharsets.UTF_8)),
                                ByteBuffer.wrap(jcBytes),
                                Expiration.from(finalExpirationTime, TimeUnit.SECONDS),
                                RedisStringCommands.SetOption.UPSERT
                        ),
                        connection.stringCommands().set(
                                ByteBuffer.wrap((USER_REFRESH_TOKEN_PREFIX + uid + DELIMITER + refreshToken).getBytes(StandardCharsets.UTF_8)),
                                ByteBuffer.wrap(jcBytes),
                                Expiration.from(finalExpirationTime, TimeUnit.SECONDS),
                                RedisStringCommands.SetOption.UPSERT
                        )
                )
        ).count().map(r -> token);
    }

    @Override
    public Mono<String> getUser(String uid) {
        return template.opsForValue().get(ByteBuffer.wrap((USER_PREFIX + uid).getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public Mono<Token> getAccessToken(String content) {
        return template
                .opsForValue()
                .get(ACCESS_TOKEN_PREFIX + content)
                .flatMap(s -> {
                    final Token token = JsonUtil.fromJson(s, Token.class);
                    return token == null ? Mono.empty() : Mono.just(token);
                });
    }

    @Override
    public Mono<Token> getRefreshToken(String content) {
        return template
                .opsForValue()
                .get(REFRESH_TOKEN_PREFIX + content)
                .flatMap(s -> {
                    final Token token = JsonUtil.fromJson(s, Token.class);
                    return token == null ? Mono.empty() : Mono.just(token);
                });
    }

    @Override
    public Mono<Long> cleanToken(String content) {
        return getAccessToken(content)
                .flatMap(t -> template.delete(
                        ACCESS_TOKEN_PREFIX + t.getAccessToken(),
                        REFRESH_TOKEN_PREFIX + t.getRefreshToken(),
                        USER_ACCESS_TOKEN_PREFIX + t.getUid() + DELIMITER + t.getAccessToken(),
                        USER_REFRESH_TOKEN_PREFIX + t.getUid() + DELIMITER + t.getRefreshToken()
                ));
    }

    @Override
    public Mono<Long> cleanUserAll(String uid) {
        return getUser(uid)
                .flatMap(s -> Flux.concat(
                        template.scan(ScanOptions.scanOptions().match(
                                USER_ACCESS_TOKEN_PREFIX + uid + DELIMITER + "*").build()),
                        template.scan(ScanOptions.scanOptions().match(
                                USER_REFRESH_TOKEN_PREFIX + uid + DELIMITER + "*").build())
                ).collectList())
                .flatMap(l -> template.delete(l.toArray(new String[0])))
                .flatMap(l -> template.delete(USER_PREFIX + uid));
    }


}
