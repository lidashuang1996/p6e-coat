package club.p6e.cloud.gateway.permission;

import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * Inject Permission Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class InjectPermissionGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

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
         * Request Header For User Permissions
         * Request Header Is Customized By The Program And Not Carried By The User Request
         * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
         */
        @SuppressWarnings("ALL")
        private static final String USER_PERMISSION_HEADER = "P6e-User-Permission";

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
        @SuppressWarnings("ALL")
        public String execute(ServerWebExchange exchange) {
            return JsonUtil.toJson(new ArrayList<>());
        }

    }

}
