package club.p6e.cloud.gateway.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Authentication Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = AuthenticationGatewayService2222.class,
        ignored = AuthenticationGatewayService2222.class
)
public class AuthenticationGatewayService2222 {

    public static String AUTHORIZATION_HEADER = "Authorization";
    public static String AUTH_HEADER_TOKEN_TYPE = "Bearer";
    public static String AUTH_HEADER_TOKEN_PREFIX = AUTH_HEADER_TOKEN_TYPE + " ";
    public static String TOKEN_REQUEST_PARAMETER = "token";
    /**
     * P6e User Auth Header Name
     */
    @SuppressWarnings("ALL")
    private static final String USER_AUTH_HEADER = "P6e-User-Auth";

    /**
     * P6e User Info Header Name
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_HEADER = "P6e-User-Info";

    /**
     * Authentication Gateway Cache Object
     */
    private final AuthenticationGatewayCache cache;

    /**
     * Constructor Initializers
     *
     * @param cache Authentication Gateway Cache Object
     */
    public AuthenticationGatewayService2222(AuthenticationGatewayCache cache) {
        this.cache = cache;
    }

    /**
     * Execute Authentication
     *
     * @param exchange Server Web Exchange Object
     * @return Server Web Exchange Object
     */
    public Mono<ServerWebExchange> execute(ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        final List<String> authorizations = request.getHeaders().get(AUTHORIZATION_HEADER);
        final List<String> tokens = new ArrayList<>(request.getQueryParams().get(TOKEN_REQUEST_PARAMETER));
        if (authorizations != null) {
            for (final String authorization : authorizations) {
                if (authorization.startsWith(AUTH_HEADER_TOKEN_TYPE)) {
                    tokens.add(authorization.substring(AUTH_HEADER_TOKEN_PREFIX.length()));
                }
            }
        }
        return cache
                .getAccessToken(token)
                .flatMap(this::executeTokenAutomaticRenewal)
                .flatMap(t -> cache.getUser(t.getUid()))
                .flatMap(u -> Mono.just(exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .header(USER_INFO_HEADER, u)
                                .header(USER_AUTH_HEADER, "1")
                                .build()
                ).build()))
                .switchIfEmpty(Mono.defer(() -> Mono.just(exchange.mutate().request(
                        exchange.getRequest().mutate().header(USER_AUTH_HEADER, "1").build()
                ).build())));
    }
}
