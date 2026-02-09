package club.p6e.cloud.gateway.filter;

import club.p6e.coat.common.exception.VoucherException;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Validate Voucher Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class ValidateVoucherGatewayFilterFactory extends AbstractGatewayFilterFactory<ValidateVoucherGatewayFilterFactory.Config> {

    /**
     * Voucher Request Parameter
     * Request To Carry Voucher Parameter
     */
    @SuppressWarnings("ALL")
    private static final String V_PARAMETER = "v";

    /**
     * Voucher Request Parameter
     * Request To Carry Voucher Parameter
     */
    @SuppressWarnings("ALL")
    private static final String VOUCHER_PARAMETER = "voucher";

    /**
     * Voucher Header Name
     * Request To Carry Voucher Request Header
     */
    @SuppressWarnings("ALL")
    private static final String X_VOUCHER_HEADER = "X-Voucher";

    /**
     * Constructor Initialization
     */
    public ValidateVoucherGatewayFilterFactory() {
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
            final ServerHttpRequest request = exchange.getRequest();
            final List<String> vouchers = new ArrayList<>();
            final List<String> vh = request.getHeaders().get(X_VOUCHER_HEADER);
            if (vh != null) {
                vouchers.addAll(vh);
            }
            final List<String> vp1 = request.getQueryParams().get(V_PARAMETER);
            if (vp1 != null) {
                vouchers.addAll(vp1);
            }
            final List<String> vp2 = request.getQueryParams().get(VOUCHER_PARAMETER);
            if (vp2 != null) {
                vouchers.addAll(vp2);
            }
            for (final String voucher : vouchers) {
                if (this.config.getVouchers().contains(voucher)) {
                    return chain.filter(exchange);
                }
            }
            return Mono.error(new VoucherException(
                    this.getClass(),
                    "fun filter(ServerWebExchange exchange, GatewayFilterChain chain)",
                    "request voucher exception"
            ));
        }

    }

    /**
     * Config
     */
    @Data
    @Accessors(chain = true)
    public static class Config implements Serializable {

        /**
         * Vouchers
         */
        private List<String> vouchers;

    }

}
