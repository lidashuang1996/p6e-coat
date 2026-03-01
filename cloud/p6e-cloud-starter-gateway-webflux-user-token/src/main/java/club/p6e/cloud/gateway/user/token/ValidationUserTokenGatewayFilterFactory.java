package club.p6e.cloud.gateway.user.token;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validation Permission Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class ValidationUserTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Validation Permission Gateway Service Object
     */
    private final ValidationUserTokenGatewayService service;

    /**
     * Constructor Initialization
     *
     * @param service Validation Permission Gateway Service Object
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
     *
     * @param service Validation Permission Gateway Service Object
     */
    public record CustomGatewayFilter(ValidationUserTokenGatewayService service) implements GatewayFilter {

        @Override
        public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, GatewayFilterChain chain) {
            return service.execute(exchange).switchIfEmpty(Mono.just(exchange)).flatMap(chain::filter);
        }

    }

}
