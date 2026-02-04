package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
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
 * Reactive Cross Domain Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveCrossDomainFilter implements WebFilter {

    /**
     * Cross Domain Header General Content
     */
    private static final String CROSS_DOMAIN_HEADER_GENERAL_CONTENT = "*";

    /**
     * Access Control Max Age
     */
    private static final long ACCESS_CONTROL_MAX_AGE = 3600L;

    /**
     * Access Control Allow Origin
     */
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "*";

    /**
     * Access Control Allow Credentials
     */
    private static final boolean ACCESS_CONTROL_ALLOW_CREDENTIALS = true;

    /**
     * Access Control Allow Headers
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
            "X-Language",
            "X-Token",
            "X-Authorization"
    };

    /**
     * Access Control Allow Methods
     */
    private static final HttpMethod[] ACCESS_CONTROL_ALLOW_METHODS = new HttpMethod[]{
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.DELETE,
            HttpMethod.OPTIONS,
    };

    /**
     * Error Result Object
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "unmatched origin cross domain requests");

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
    public ReactiveCrossDomainFilter(Properties properties) {
        this.properties = properties;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final Properties.CrossDomain crossDomain = properties.getCrossDomain();
        if (crossDomain != null && crossDomain.isEnable()) {
            final ServerHttpRequest request = exchange.getRequest();
            final ServerHttpResponse response = exchange.getResponse();
            String origin = ReactiveWebUtil.getHeader(request, HttpHeaders.ORIGIN);
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
     * Validation Origin
     *
     * @param origin    origin
     * @param whiteList whiteList
     * @return true or false
     */
    public boolean validationOrigin(String origin, List<String> whiteList) {
        if (whiteList != null && !whiteList.isEmpty()) {
            for (final String item : whiteList) {
                if (item.equals(CROSS_DOMAIN_HEADER_GENERAL_CONTENT) || origin.startsWith(item)) {
                    return true;
                }
            }
        }
        return false;
    }

}
