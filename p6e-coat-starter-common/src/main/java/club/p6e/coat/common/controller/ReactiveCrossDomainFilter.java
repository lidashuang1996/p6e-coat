package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.WebUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
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
     * Error Result Object
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "mismatched origin cross domain request");

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
            String origin = WebUtil.getHeader(request, HttpHeaders.ORIGIN);
            if (validationOrigin(origin, List.copyOf(crossDomain.getWhiteList()))) {
                response.getHeaders().setAccessControlAllowOrigin(origin == null ? "*" : origin);
                response.getHeaders().setAccessControlMaxAge(getAccessControlMaxAge());
                response.getHeaders().setAccessControlAllowMethods(getAccessControlAllowMethods());
                response.getHeaders().setAccessControlAllowHeaders(getAccessControlAllowHeaders());
                response.getHeaders().setAccessControlAllowCredentials(getAccessControlAllowCredentials());
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
                if (item.equals("*") || origin.startsWith(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Access Control Max Age
     *
     * @return Access Control Max Age
     */
    public static long getAccessControlMaxAge() {
        return 3600L;
    }

    /**
     * Access Control Allow Credentials
     *
     * @return Access Control Allow Credentials
     */
    public static boolean getAccessControlAllowCredentials() {
        return true;
    }

    /**
     * Access Control Allow Methods
     *
     * @return Access Control Allow Methods
     */
    public static List<HttpMethod> getAccessControlAllowMethods() {
        return List.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS);
    }

    /**
     * Access Control Allow Headers
     *
     * @return Access Control Allow Headers
     */
    public static List<String> getAccessControlAllowHeaders() {
        return List.of("Accept", "Host", "Origin", "Referer", "User-Agent", "Content-Type", "Authorization", "X-Project", "X-Voucher", "X-Language", "X-Token", "X-Authorization");
    }

}
