package club.p6e.coat.common.controller.filter;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.controller.BaseWebFluxController;
import club.p6e.coat.common.utils.JsonUtil;
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
 * 凭证认证过滤器
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class VoucherWebFluxFilter implements WebFilter {

    /**
     * 凭证的头部
     */
    private static final String VOUCHER_HEADER = "P6e-Voucher";

    /**
     * 错误结果对象
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "Access with invalid voucher");

    /**
     * 错误结果文本内容
     */
    private static final String ERROR_RESULT_CONTENT = JsonUtil.toJson(ERROR_RESULT);

    /**
     * 配置对象
     */
    private final Properties properties;

    /**
     * 构造方法初始化
     *
     * @param properties 配置对象
     */
    public VoucherWebFluxFilter(Properties properties) {
        this.properties = properties;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final Properties.Security security = properties.getSecurity();
        if (security != null && security.isEnable()) {
            final ServerHttpRequest request = exchange.getRequest();
            final ServerHttpResponse response = exchange.getResponse();
            String voucher = BaseWebFluxController.getHeader(request, VOUCHER_HEADER);
            if (voucher != null) {
                for (final String item : security.getVouchers()) {
                    if (item.equalsIgnoreCase(voucher)) {
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
