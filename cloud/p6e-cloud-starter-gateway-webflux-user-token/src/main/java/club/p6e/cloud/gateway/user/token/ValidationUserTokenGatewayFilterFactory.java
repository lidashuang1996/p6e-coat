package club.p6e.cloud.gateway.user.token;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validation User Token Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class ValidationUserTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Validation User Token Gateway Service Object
     */
    private final ValidationUserTokenGatewayService service;

    /**
     * Constructor Initialization
     *
     * @param service Validation User Token Gateway Service Object
     */
    public ValidationUserTokenGatewayFilterFactory(ValidationUserTokenGatewayService service) {
        this.service = service;
    }

    @Override
    public @NonNull GatewayFilter apply(Object config) {
        return new CustomGatewayFilter(service);
    }

    /**
     * Custom Gateway Filter
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        /**
         * Validation User Token Gateway Service Object
         */
        private final ValidationUserTokenGatewayService service;

        /**
         * Constructor Initialization
         *
         * @param service Validation User Token Gateway Service Object
         */
        public CustomGatewayFilter(ValidationUserTokenGatewayService service) {
            this.service = service;
        }

        @Override
        public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, GatewayFilterChain chain) {
            // token is empty or validation degraded → pass through without auth header,
            // downstream services decide whether to reject unauthenticated requests
            return service.execute(exchange).switchIfEmpty(Mono.just(exchange)).flatMap(chain::filter);
        }

    }

}
