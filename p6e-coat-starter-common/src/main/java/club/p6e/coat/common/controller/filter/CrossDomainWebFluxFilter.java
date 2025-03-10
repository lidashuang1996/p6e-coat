package club.p6e.coat.common.controller.filter;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.controller.BaseWebFluxController;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 跨域过滤器
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CrossDomainWebFluxFilter implements WebFilter {

    /**
     * 通用内容
     */
    private static final String CROSS_DOMAIN_HEADER_GENERAL_CONTENT = "*";

    /**
     * 跨域配置 ACCESS_CONTROL_MAX_AGE
     */
    private static final long ACCESS_CONTROL_MAX_AGE = 3600L;

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_ORIGIN
     */
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "*";

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_ORIGIN
     */
    private static final boolean ACCESS_CONTROL_ALLOW_CREDENTIALS = true;

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_HEADERS
     */
    private static final String[] ACCESS_CONTROL_ALLOW_HEADERS = new String[]{
            "Accept",
            "Host",
            "Origin",
            "Referer",
            "User-Agent",
            "Content-Type",
            "Authorization",
            "X-Project",
            "X-Voucher",
            "X-Language"
    };

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_METHODS
     */
    private static final HttpMethod[] ACCESS_CONTROL_ALLOW_METHODS = new HttpMethod[]{
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.DELETE,
            HttpMethod.OPTIONS,
    };

    /**
     * 错误结果对象
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "Unmatched origin cross domain requests");

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
    public CrossDomainWebFluxFilter(Properties properties) {
        this.properties = properties;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final Properties.CrossDomain crossDomain = properties.getCrossDomain();
        if (crossDomain != null && crossDomain.isEnable()) {
            final ServerHttpRequest request = exchange.getRequest();
            final ServerHttpResponse response = exchange.getResponse();
            String origin = BaseWebFluxController.getHeader(request, HttpHeaders.ORIGIN);
            if (validationOrigin(origin, List.of(crossDomain.getWhiteList()))) {
                response.getHeaders().setAccessControlMaxAge(ACCESS_CONTROL_MAX_AGE);
                response.getHeaders().setAccessControlAllowOrigin(origin == null ? ACCESS_CONTROL_ALLOW_ORIGIN : origin);
                response.getHeaders().setAccessControlAllowCredentials(ACCESS_CONTROL_ALLOW_CREDENTIALS);
                response.getHeaders().setAccessControlAllowHeaders(Arrays.asList(ACCESS_CONTROL_ALLOW_HEADERS));
                response.getHeaders().setAccessControlAllowMethods(Arrays.asList(ACCESS_CONTROL_ALLOW_METHODS));
                if (HttpMethod.OPTIONS.matches(request.getMethod().name().toUpperCase())) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
                return chain.filter(exchange.mutate().response(response).build());
            } else {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.writeWith(Mono.just(response.bufferFactory()
                        .wrap(ERROR_RESULT_CONTENT.getBytes(StandardCharsets.UTF_8))));
            }
        } else {
            return chain.filter(exchange);
        }
    }


    /**
     * 验证 origin 是否合法
     *
     * @param origin    origin
     * @param whiteList whiteList
     * @return true or false
     */
    public boolean validationOrigin(String origin, List<String> whiteList) {
        if (whiteList != null && !whiteList.isEmpty()) {
            for (final String item : whiteList) {
                if (item.equalsIgnoreCase(CROSS_DOMAIN_HEADER_GENERAL_CONTENT) || origin.startsWith(item)) {
                    return true;
                }
            }
        }
        return false;
    }

}
