package club.p6e.cloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 Encoder Header Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class Base64EncoderHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter();
    }

    /**
     * Custom Gateway Filter
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            final HttpHeaders httpHeaders = request.getHeaders();
            final ServerHttpRequest.Builder builder = request.mutate();
            for (final String name : httpHeaders.keySet()) {
                // all request headers starting from p6e-
                if (name != null && name.toLowerCase().startsWith("p6e-")) {
                    final String value = httpHeaders.getFirst(name.toLowerCase());
                    if (value != null) {
                        // base64 encoder
                        builder.header(name, Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)));
                    }
                }
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

    }

}
