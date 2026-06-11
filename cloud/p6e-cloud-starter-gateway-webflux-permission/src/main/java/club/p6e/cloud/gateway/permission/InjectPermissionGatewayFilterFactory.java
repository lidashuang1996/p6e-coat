package club.p6e.cloud.gateway.permission;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Inject Permission Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class InjectPermissionGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @NonNull
    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter();
    }

    /**
     * Custom Gateway Filter
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        /**
         * Premission Header Name
         */
        @SuppressWarnings("ALL")
        private static final String USER_PERMISSION_HEADER = "P6e-User-Permission";

        @NonNull
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            return chain.filter(exchange.mutate().request(
                    request.mutate().header(USER_PERMISSION_HEADER, execute(exchange)).build()
            ).build());
        }

        /**
         * Get Permission Data Serialize
         *
         * @param exchange Server Web Exchange Object
         * @return Permission Data Serialize String Object
         */
        public String execute(ServerWebExchange exchange) {
            throw new RuntimeException("Inject Permission Gateway Filter Factory Is Not Implemented.");
        }

    }

}
