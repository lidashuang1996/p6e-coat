package club.p6e.coat.auth.client.cache.redis;

import club.p6e.coat.auth.client.cache.AuthCacheReactive;
import club.p6e.coat.auth.client.cache.redis.support.RedisCacheReactive;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
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
public class AuthRedisCacheReactive extends RedisCacheReactive implements AuthCacheReactive {

    /**
     * 缓存对象
     */
    private final ReactiveStringRedisTemplate template;

    /**
     * 构造方法初始化
     *
     * @param template 缓存对象
     */
    public AuthRedisCacheReactive(ReactiveStringRedisTemplate template) {
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
            final byte[] jcBytes = json.getBytes(StandardCharsets.UTF_8);
            return template.execute(connection ->
                    Flux.concat(
                            connection.stringCommands().set(
                                    ByteBuffer.wrap((USER_PREFIX + uid).getBytes(StandardCharsets.UTF_8)),
                                    ByteBuffer.wrap(user.getBytes(StandardCharsets.UTF_8)),
                                    Expiration.from(EXPIRATION_TIME, TimeUnit.SECONDS),
                                    RedisStringCommands.SetOption.UPSERT
                            ),
                            connection.stringCommands().set(
                                    ByteBuffer.wrap((ACCESS_TOKEN_PREFIX + accessToken).getBytes(StandardCharsets.UTF_8)),
                                    ByteBuffer.wrap(jcBytes),
                                    Expiration.from(EXPIRATION_TIME, TimeUnit.SECONDS),
                                    RedisStringCommands.SetOption.UPSERT
                            ),
                            connection.stringCommands().set(
                                    ByteBuffer.wrap((REFRESH_TOKEN_PREFIX + refreshToken).getBytes(StandardCharsets.UTF_8)),
                                    ByteBuffer.wrap(jcBytes),
                                    Expiration.from(EXPIRATION_TIME, TimeUnit.SECONDS),
                                    RedisStringCommands.SetOption.UPSERT
                            ),
                            connection.stringCommands().set(
                                    ByteBuffer.wrap((USER_ACCESS_TOKEN_PREFIX + uid + DELIMITER + accessToken).getBytes(StandardCharsets.UTF_8)),
                                    ByteBuffer.wrap(jcBytes),
                                    Expiration.from(EXPIRATION_TIME, TimeUnit.SECONDS),
                                    RedisStringCommands.SetOption.UPSERT
                            ),
                            connection.stringCommands().set(
                                    ByteBuffer.wrap((USER_REFRESH_TOKEN_PREFIX + uid + DELIMITER + refreshToken).getBytes(StandardCharsets.UTF_8)),
                                    ByteBuffer.wrap(jcBytes),
                                    Expiration.from(EXPIRATION_TIME, TimeUnit.SECONDS),
                                    RedisStringCommands.SetOption.UPSERT
                            )
                    )
            ).collectList().map(r -> token);
        }
    }

    @Override
    public Mono<String> getUser(String id) {
        return template.opsForValue().get(USER_PREFIX + id);
    }

    @Override
    public Mono<Token> getAccessToken(String token) {
        return template.opsForValue()
                .get(ACCESS_TOKEN_PREFIX + token)
                .flatMap(c -> {
                    final Token t = JsonUtil.fromJson(c, Token.class);
                    return t == null ? Mono.empty() : Mono.just(t);
                });
    }

    @Override
    public Mono<Token> getRefreshToken(String token) {
        return template.opsForValue()
                .get(REFRESH_TOKEN_PREFIX + token)
                .flatMap(c -> {
                    final Token t = JsonUtil.fromJson(c, Token.class);
                    return t == null ? Mono.empty() : Mono.just(t);
                });
    }

    @Override
    public Mono<Long> cleanAccessToken(String token) {
        return getAccessToken(token)
                .flatMap(t -> template.delete(
                        ACCESS_TOKEN_PREFIX + t.getAccessToken(),
                        REFRESH_TOKEN_PREFIX + t.getRefreshToken()
                ));
    }

}
