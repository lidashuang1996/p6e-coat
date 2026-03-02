package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.auth.UserBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Validation User Token Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(ValidationUserTokenGatewayService.class)
public class ValidationUserTokenGatewayService {

    /**
     * User Info Header Name
     * Request Header For User Information
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_HEADER = "P6e-User-Info";

    /**
     * Authentication Header Name
     * Request Header For Authentication
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String AUTHENTICATION_HEADER = "P6e-Authentication";

    /**
     * Token Param
     */
    private static final String TOKEN_PARAM = "token";

    /**
     * Auth Header
     */
    private static final String AUTH_HEADER = "Authorization";

    /**
     * Auth Header Type
     */
    private static final String AUTH_HEADER_TOKEN_TYPE = "Bearer";

    /**
     * Auth Header Token Prefix
     */
    private static final String AUTH_HEADER_TOKEN_PREFIX = AUTH_HEADER_TOKEN_TYPE + " ";

    /**
     * User Toke Object
     */
    private final UserBuilder builder;

    /**
     * User Repository Object
     */
    private final UserRepository userRepository;

    /**
     * User Token Repository Object
     */
    private final UserTokenRepository userTokenRepository;

    /**
     * Reactive Local Storage Cache Token Validator Object
     */
    private final UserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param builder             User Builder Object
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
     * @return User Token Object
     */
    private static String getUserToken(ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        final HttpHeaders headers = request.getHeaders();
        final MultiValueMap<String, String> params = request.getQueryParams();
        String token = params.getFirst(TOKEN_PARAM);
        if (token == null) {
            final String ahv = headers.getFirst(AUTH_HEADER);
            if (ahv != null && ahv.startsWith(AUTH_HEADER_TOKEN_PREFIX)) {
                token = ahv.substring(AUTH_HEADER_TOKEN_PREFIX.length());
            }
        }
        return token;
    }

    /**
     * Execute User Token Service
     *
     * @param exchange Server Web Exchange Object
     * @return Mono<ServerWebExchange> Server Web Exchange Object
     */
    public Mono<ServerWebExchange> execute(ServerWebExchange exchange) {
        String token = getUserToken(exchange);
        if (token != null && token.startsWith("p6e@")) {
            return cache.verification(token).switchIfEmpty(userTokenRepository.get(token).flatMap(m -> {
                if (m.getEndDateTime().isAfter(m.getStartDateTime())) {
                    final long endEpochMilli = m.getEndDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    final long nowEpochMilli = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    final long startEpochMilli = m.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    if (startEpochMilli <= nowEpochMilli && endEpochMilli > nowEpochMilli) {
                        final long timeout = endEpochMilli - nowEpochMilli >= 3600 * 1000L ? 3600 : (endEpochMilli - nowEpochMilli) / 1000L;
                        return userRepository.get(m.getUid()).map(builder::create).flatMap(u -> cache.register(token, u.serialize(), timeout));
                    }
                }
                return Mono.empty();
            })).map(s -> exchange.mutate().request(exchange.getRequest().mutate().header(USER_INFO_HEADER, s).header(AUTHENTICATION_HEADER, "1").build()).build());
        } else {
            return Mono.empty();
        }
    }

    /**
     * User Token Cache
     *
     * @param template Token
     */
    private record UserTokenCache(ReactiveStringRedisTemplate template) {
        private static final String PRESET_USER_TOKEN_CACHE_PREFIX = "PRESET_USER_TOKEN:";

        public Mono<String> register(String token, String content, long timeout) {
            return template.opsForValue().set(PRESET_USER_TOKEN_CACHE_PREFIX + token, content, Duration.ofSeconds(timeout)).map(_ -> token);
        }

        public Mono<String> verification(String token) {
            return template.opsForValue().get(ByteBuffer.wrap((PRESET_USER_TOKEN_CACHE_PREFIX + token).getBytes(StandardCharsets.UTF_8)));
        }

    }

}
