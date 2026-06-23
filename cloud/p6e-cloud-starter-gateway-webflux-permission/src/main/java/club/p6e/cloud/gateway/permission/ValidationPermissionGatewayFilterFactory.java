package club.p6e.cloud.gateway.permission;

import club.p6e.coat.common.exception.PermissionException;
import club.p6e.coat.common.utils.JsonUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validation Permission Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class ValidationPermissionGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Validation Permission Gateway Service Object
     */
    private final ValidationPermissionGatewayService service;

    /**
     * Constructor Initialization
     *
     * @param service Validation Permission Gateway Service Object
     */
    public ValidationPermissionGatewayFilterFactory(ValidationPermissionGatewayService service) {
        this.service = service;
    }

    @NonNull
    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter(service);
    }

    /**
     * Custom Gateway Filter
     *
     * @param service Validation Permission Gateway Service Object
     */
    public record CustomGatewayFilter(ValidationPermissionGatewayService service) implements GatewayFilter {

        /**
         * Permission Header Name (Internal Request Header)
         * Custom HTTP Header Name, Non Standard RFC Header
         */
        @SuppressWarnings("ALL")
        private static final String PERMISSION_HEADER = "P6e-Permission";

        @NonNull
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            return service.execute(exchange).flatMap(p -> {
                final String json = JsonUtil.toJson(p);
                if (json == null) {
                    return Mono.error(new PermissionException(
                            ValidationPermissionGatewayFilterFactory.class,
                            "fun filter(ServerWebExchange exchange, GatewayFilterChain chain)",
                            "check permission appear serialize permission details error"
                    ));
                }
                return chain.filter(exchange.mutate().request(request.mutate().header(PERMISSION_HEADER, json).build()).build());
            });
        }

    }

}
