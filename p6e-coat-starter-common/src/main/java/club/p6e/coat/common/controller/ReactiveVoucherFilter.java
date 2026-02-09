package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.WebUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Reactive Voucher Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveVoucherFilter implements WebFilter {

    /**
     * Error Result Object
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "invalid voucher access");

    /**
     * Error Result Content Object
     */
    private static final String ERROR_RESULT_CONTENT = JsonUtil.toJson(ERROR_RESULT);

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     */
    public ReactiveVoucherFilter(Properties properties) {
        this.properties = properties;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final Properties.Security security = properties.getSecurity();
        if (security != null && security.isEnable()) {
            final ServerHttpRequest request = exchange.getRequest();
            final ServerHttpResponse response = exchange.getResponse();
            final String voucher = WebUtil.getHeader(request, security.getHeader());
            if (voucher != null) {
                for (final String item : security.getVouchers()) {
                    if (item.equals(voucher)) {
                        return chain.filter(exchange);
                    }
                }
            }
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.just(response.bufferFactory()
                    .wrap(ERROR_RESULT_CONTENT.getBytes(StandardCharsets.UTF_8))));
        } else {
            return chain.filter(exchange);
        }
    }

}
