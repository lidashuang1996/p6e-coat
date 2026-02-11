package club.p6e.cloud.gateway.permission;

import club.p6e.coat.common.utils.JsonUtil;
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
         * Permission Header
         * Save The Request Header Of The Permission Information Used In The Current Request
         * Request Header Is Customized By The Program And Not Carried By The User Request
         * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
         */
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
