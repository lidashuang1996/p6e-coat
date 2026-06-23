package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.auth.UserBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Validation User Token Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@ConditionalOnMissingBean(ValidationUserTokenGatewayService.class)
public class ValidationUserTokenGatewayService {

    /**
     * Token Param
     */
    private static final String TOKEN_PARAM = "token";

    /**
     * Token Header (External Request Headers)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String TOKEN_HEADER = "X-Token";

    /**
     * User Info Header Name (Internal Request Header)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_HEADER = "P6e-User-Info";

    /**
     * Authentication Header Name (Internal Request Header)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String AUTHENTICATION_HEADER = "P6e-Authentication";

    /**
     * User Builder Object
     */
    private final UserBuilder builder;

    /**
     * User Token Cache Object
     */
    private final UserTokenCache cache;

    /**
     * User Repository Object
     */
    private final UserRepository userRepository;

    /**
     * User Token Repository Object
     */
    private final UserTokenRepository userTokenRepository;

    /**
     * Constructor Initialization
     *
     * @param builder             User Builder Object
     * @param template            Reactive String Redis Template Object
     * @param userRepository      User Repository Object
     * @param userTokenRepository User Token Repository Object
     */
    public ValidationUserTokenGatewayService(
            UserBuilder builder,
            ReactiveStringRedisTemplate template,
            UserRepository userRepository,
            UserTokenRepository userTokenRepository
    ) {
        this.builder = builder;
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.cache = new UserTokenCache(template);
    }

    /**
     * Get User Token Object
     *
     * @param exchange Server Web Exchange Object
     * @return User Token
     */
    private static String getUserToken(ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        final HttpHeaders headers = request.getHeaders();
        final MultiValueMap<String, String> params = request.getQueryParams();
        return Stream.of(params.getFirst(TOKEN_PARAM), headers.getFirst(TOKEN_HEADER)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    /**
     * Execute User Token Service
     *
     * @param exchange Server Web Exchange Object
     * @return Mono<ServerWebExchange> Server Web Exchange Object
     */
    public Mono<ServerWebExchange> execute(ServerWebExchange exchange) {
        final String token = getUserToken(exchange);
        if (token == null) {
            return Mono.empty();
        } else {
            return cache
                    .verification(token)
                    .onErrorResume(e -> {
                        log.warn("user token redis unavailable, skip cache: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .switchIfEmpty(userTokenRepository
                            .get(token)
                            .onErrorResume(e -> {
                                log.warn("user token db query failed, skip validation: {}", e.getMessage());
                                return Mono.empty();
                            })
                            .flatMap(m -> {
                                if (m.getStartDateTime() != null && m.getEndDateTime() != null) {
                                    final long endEpochMilli = m.getEndDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                    final long nowEpochMilli = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                    final long startEpochMilli = m.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                    if (startEpochMilli <= nowEpochMilli && nowEpochMilli < endEpochMilli) {
                                        final long remaining = (endEpochMilli - nowEpochMilli) / 1000L;
                                        final long timeout = Math.min(remaining, 3600L);
                                        return userRepository.get(m.getUid())
                                    .onErrorResume(e -> {
                                        log.warn("user data query failed, skip validation: {}", e.getMessage());
                                        return Mono.empty();
                                    })
                                    .map(builder::create)
                                    .flatMap(u -> cache.register(token, u.serialize(), timeout));
                                    }
                                }
                                return Mono.empty();
                            })).map(s -> exchange.mutate().request(exchange.getRequest().mutate()
                            .header(USER_INFO_HEADER, s)
                            .header(AUTHENTICATION_HEADER, "1").build()
                    ).build());
        }
    }

    /**
     * User Token Cache
     */
    @SuppressWarnings("ALL")
    private static class UserTokenCache {

        /**
         * User Token Cache Prefix
         */
        private static final String PRESET_USER_TOKEN_CACHE_PREFIX = "PRESET_USER_TOKEN:";

        /**
         * Reactive String Redis Template Object
         */
        private final ReactiveStringRedisTemplate template;

        /**
         * Constructor Initialization
         *
         * @param template Reactive String Redis Template Object
         */
        public UserTokenCache(ReactiveStringRedisTemplate template) {
            this.template = template;
        }

        /**
         * Register
         *
         * @param token   User Token
         * @param content User Content
         * @param timeout User Timeout
         * @return Mono<String> User Token
         */
        public Mono<String> register(String token, String content, long timeout) {
            return template
                    .opsForValue()
                    .set(PRESET_USER_TOKEN_CACHE_PREFIX + token, content, Duration.ofSeconds(timeout))
                    .map(_ -> content);
        }

        /**
         * Verification
         *
         * @param token User Token
         * @return Mono<String> User Token
         */
        public Mono<String> verification(String token) {
            return template.opsForValue().get(PRESET_USER_TOKEN_CACHE_PREFIX + token);
        }

    }

}
