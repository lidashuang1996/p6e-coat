package club.p6e.cloud.gateway.auth;

import club.p6e.coat.common.exception.AuthException;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validation Authentication Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class ValidationAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public @NonNull GatewayFilter apply(Object config) {
        return new CustomGatewayFilter();
    }

    /**
     * Custom Gateway Filter
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        /**
         * User Info Header Name (Internal Request Header)
         * Custom HTTP Header Name, Non Standard RFC Header
         */
        @SuppressWarnings("ALL")
        private static final String USER_INFO_HEADER = "P6e-User-Info";

        /**
         * Permission Header Name (Internal Request Header)
         * Custom HTTP Header Name, Non Standard RFC Header
         */
        @SuppressWarnings("ALL")
        private static final String PERMISSION_HEADER = "P6e-Permission";

        /**
         * Authentication Header Name (Internal Request Header)
         * Custom HTTP Header Name, Non Standard RFC Header
         */
        @SuppressWarnings("ALL")
        private static final String AUTHENTICATION_HEADER = "P6e-Authentication";

        @Override
        public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            final String userInfo = request.getHeaders().getFirst(USER_INFO_HEADER);
            final String permission = request.getHeaders().getFirst(PERMISSION_HEADER);
            final String authentication = request.getHeaders().getFirst(AUTHENTICATION_HEADER);
            final boolean status = authentication != null && !authentication.isEmpty();
            if (status) {
                // 1 / MQ==
                // determine whether the user information has been written into the request
                // due to the possibility of user information being messy code, it may be encoded using base64 or not encoded at all
                final boolean status0 = "0".equals(authentication) || "MA==".equals(authentication);
                final boolean status1 = "1".equals(authentication) || "MQ==".equals(authentication);
                if (status1 && userInfo != null && !userInfo.isEmpty()) {
                    // login user access
                    return chain.filter(exchange);
                } else if (status0 && userInfo != null && !userInfo.isEmpty() && permission != null && !permission.isEmpty()) {
                    // anonymous user access
                    // current access permission is required
                    return chain.filter(exchange);
                }
            }
            return Mono.error(new AuthException(
                    this.getClass(),
                    "fun filter(ServerWebExchange exchange, GatewayFilterChain chain)",
                    "request authentication exception"
            ));
        }

    }

}
