package club.p6e.coat.permission.filter;

import club.p6e.coat.common.exception.PermissionException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.validator.PermissionValidator;
import org.jspecify.annotations.NonNull;
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
public class ReactivePermissionFilter implements WebFilter {

    /**
     * Permission Header (Internal Request Header)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String PERMISSION_HEADER = "P6e-Permission";

    /**
     * User Permission Header (Internal Request Header)
     * Custom HTTP Header Name, Non Standard RFC Header
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

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final PermissionDetails details = validate(request);
        if (details == null) {
            return Mono.error(new PermissionException(
                    this.getClass(),
                    "fun Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)",
                    "request permission exception"
            ));
        } else {
            exchange.mutate().request(request.mutate().header(
                    PERMISSION_HEADER, JsonUtil.toJson(details)).build()
            ).build();
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
        final String user = request.getHeaders().getFirst(USER_PERMISSION_HEADER);
        if (user != null) {
            // the user source of this place is the internal request header
            // please ensure the security of internal request header
            final List<String> data = JsonUtil.fromJsonToList(user, String.class);
            if (data != null) {
                permissions.addAll(data);
            }
        }
        return this.validator.execute(path, method, permissions);
    }

}
