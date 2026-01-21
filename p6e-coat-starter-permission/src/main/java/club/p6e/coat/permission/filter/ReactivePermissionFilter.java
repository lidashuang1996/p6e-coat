package club.p6e.coat.permission.filter;

import club.p6e.coat.common.error.PermissionException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.validator.PermissionValidator;
import jakarta.annotation.Nonnull;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Reactive Permission Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactivePermissionFilter implements WebFilter, Ordered {

    /**
     * Permission Header
     * Save The Request Header Of The Permission Information Used In The Current Request
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String PERMISSION_HEADER = "P6e-Permission";

    /**
     * User Permission Header
     * Request Header For Saving User Owned Permission Group
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String USER_PERMISSION_HEADER = "P6e-User-Permission";

    /**
     * Permission Validator Object
     */
    private final PermissionValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Permission Validator Object
     */
    public ReactivePermissionFilter(PermissionValidator validator) {
        this.validator = validator;
    }

    @Override
    public int getOrder() {
        return 20000;
    }

    @Nonnull
    @Override
    public Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final PermissionDetails details = validate(request);
        if (details == null) {
            return Mono.error(new PermissionException(
                    this.getClass(),
                    "filter(ServerWebExchange exchange, WebFilterChain chain)",
                    "request permission exception"
            ));
        } else {
            exchange.mutate().request(request.mutate().header(
                    PERMISSION_HEADER, JsonUtil.toJson(details)).build()).build();
            return chain.filter(exchange);
        }
    }

    /**
     * Validate Request Permission
     *
     * @param request Server Http Request Object
     * @return Permission Details Object
     */
    public PermissionDetails validate(ServerHttpRequest request) {
        final List<String> permissions = new ArrayList<>();
        final String path = request.getPath().value();
        final String method = request.getMethod().name().toUpperCase();
        final List<String> list = request.getHeaders().get(USER_PERMISSION_HEADER);
        if (list != null) {
            for (final String item : list) {
                final List<String> data = JsonUtil.fromJsonToList(item, String.class);
                if (data != null) {
                    permissions.addAll(data);
                }
            }
        }
        return this.validator.execute(path, method, permissions);
    }

}
