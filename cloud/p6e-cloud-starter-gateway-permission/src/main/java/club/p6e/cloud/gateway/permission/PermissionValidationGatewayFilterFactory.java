package club.p6e.cloud.gateway.permission;

import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Permission Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class PermissionValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Permission Validation Gateway Service Object
     */
    private final PermissionValidationGatewayService service;

    /**
     * Constructor Initialization
     *
     * @param service Permission Validation Gateway Service Object
     */
    public PermissionValidationGatewayFilterFactory(PermissionValidationGatewayService service) {
        this.service = service;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter(service);
    }

    /**
     * Custom Gateway Filter
     *
     * @param service Permission Validation Gateway Service Object
     */
    private record CustomGatewayFilter(PermissionValidationGatewayService service) implements GatewayFilter {

        @SuppressWarnings("ALL")
        private static final String PERMISSION_HEADER = "P6e-Permission";

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            return service.execute(exchange).flatMap(p ->
                    chain.filter(exchange.mutate().request(request.mutate()
                            .header(PERMISSION_HEADER, JsonUtil.toJson(p)).build()).build()));
        }

    }

}
