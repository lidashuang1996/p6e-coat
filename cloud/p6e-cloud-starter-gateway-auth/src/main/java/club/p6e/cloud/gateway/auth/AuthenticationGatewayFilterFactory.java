package club.p6e.cloud.gateway.auth;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Authentication Gateway Service Object
     */
    private final AuthenticationGatewayService service;

    /**
     * Constructor Initializers
     *
     * @param service Authentication Gateway Service object
     */
    public AuthenticationGatewayFilterFactory(AuthenticationGatewayService service) {
        this.service = service;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter(service);
    }

    /**
     * Custom Gateway Filter
     *
     * @param service Authentication Gateway Service Object
     */
    private record CustomGatewayFilter(AuthenticationGatewayService service) implements GatewayFilter {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return service.execute(exchange).switchIfEmpty(Mono.just(exchange)).flatMap(chain::filter);
        }

    }

}
