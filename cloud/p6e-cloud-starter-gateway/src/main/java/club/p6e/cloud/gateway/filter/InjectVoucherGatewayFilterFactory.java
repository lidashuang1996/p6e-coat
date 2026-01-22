package club.p6e.cloud.gateway.filter;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Inject Voucher Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class InjectVoucherGatewayFilterFactory extends AbstractGatewayFilterFactory<InjectVoucherGatewayFilterFactory.Config> {

    /**
     * Constructor Initialization
     */
    public InjectVoucherGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new CustomGatewayFilter(config);
    }

    /**
     * Custom Gateway Filter
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        /**
         * Config Object
         */
        private final Config config;

        /**
         * Constructor Initialization
         *
         * @param config Config object
         */
        public CustomGatewayFilter(Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            final ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            if (this.config != null && this.config.getKey() != null && this.config.getValue() != null) {
                final String key = this.config.getKey().trim();
                final String value = this.config.getValue().trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    builder.header(key, value);
                }
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

    }

    /**
     * Config
     */
    @Data
    @Accessors(chain = true)
    public static class Config implements Serializable {

        /**
         * Voucher Key
         */
        private String key;

        /**
         * Voucher Value
         */
        private String value;

    }

}
