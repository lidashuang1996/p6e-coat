package club.p6e.cloud.gateway.auth;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.JsonUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Inject Authentication Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class InjectAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Inject Authentication Gateway Service Object
     */
    private final InjectAuthenticationGatewayService service;

    /**
     * Constructor Initialization
     *
     * @param service Inject Authentication Gateway Service Object
     */
    public InjectAuthenticationGatewayFilterFactory(InjectAuthenticationGatewayService service) {
        this.service = service;
    }

    @Override
    public @NonNull GatewayFilter apply(Object config) {
        return new CustomGatewayFilter(service);
    }

    /**
     * Custom Gateway Filter
     *
     * @param service Inject Authentication Gateway Service Object
     */
    public record CustomGatewayFilter(InjectAuthenticationGatewayService service) implements GatewayFilter {

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
         * Anonymous User
         */
        private static final User ANONYMOUS = new User() {
            @Override
            public String id() {
                return "0";
            }

            @Override
            public String password() {
                return "";
            }

            @Override
            public User password(String password) {
                return this;
            }

            @Override
            public String serialize() {
                return JsonUtil.toJson(toMap());
            }

            @Override
            public Map<String, Object> toMap() {
                return Map.of("id", "0");
            }
        };

        @Override
        public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            final String userInfo = request.getHeaders().getFirst(USER_INFO_HEADER);
            final String authentication = request.getHeaders().getFirst(AUTHENTICATION_HEADER);
            final boolean status = authentication != null && !authentication.isEmpty()
                    // 1 / MQ==
                    // determine whether the user information has been written into the request
                    // due to the possibility of user information being messy code, it may be encoded using base64 or not encoded at all
                    && ("1".equals(authentication) || "MQ==".equals(authentication));
            if (status && userInfo != null && !userInfo.isEmpty()) {
                return chain.filter(exchange);
            } else {
                return service
                        .execute(exchange)
                        .defaultIfEmpty(ANONYMOUS)
                        .flatMap(u -> chain.filter(exchange.mutate().request(exchange
                                .getRequest().mutate()
                                .header(USER_INFO_HEADER, u.serialize())
                                .header(AUTHENTICATION_HEADER, ANONYMOUS.id().equals(u.id()) ? "0" : "1").build()
                        ).build()));
            }
        }

    }

}
