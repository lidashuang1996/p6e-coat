package club.p6e.cloud.gateway.auth;

import club.p6e.coat.common.error.AuthException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Global Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(AuthenticationGlobalFilter.class)
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {

    /**
     * ORDER
     */
    private static final int ORDER = Integer.MAX_VALUE - 3000;

    /**
     * User Info Header Name
     * Request Header For User Information
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_HEADER = "P6e-User-Info";

    /**
     * Permission Header Name
     * Save The Request Header Of The Permission Information Used In The Current Request
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String PERMISSION_HEADER = "P6e-Permission";

    /**
     * Authentication Header Name
     * Request Header For Authentication
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String AUTHENTICATION_HEADER = "P6e-Authentication";

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final String userInfo = request.getHeaders().getFirst(USER_INFO_HEADER);
        final String permission = request.getHeaders().getFirst(PERMISSION_HEADER);
        final String authentication = request.getHeaders().getFirst(AUTHENTICATION_HEADER);
        final boolean status = authentication != null && !authentication.isEmpty();
        if (status) {
            if (("1".equalsIgnoreCase(authentication) || "MQ==".equalsIgnoreCase(authentication)) && userInfo != null && !userInfo.isEmpty()) {
                // login user access
                return chain.filter(exchange);
            } else if (("0".equalsIgnoreCase(authentication) || "MA==".equalsIgnoreCase(authentication)) && userInfo != null && !userInfo.isEmpty() && permission != null && !permission.isEmpty()) {
                // anonymous user access
                // current access permission is required
                return chain.filter(exchange);
            }
            return Mono.error(new AuthException(
                    this.getClass(),
                    "fun filter(ServerWebExchange exchange, GatewayFilterChain chain).",
                    "request authentication exception."
            ));
        } else {
            return chain.filter(exchange);
        }
    }

}
