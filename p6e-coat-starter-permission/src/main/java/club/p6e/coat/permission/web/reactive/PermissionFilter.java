package club.p6e.coat.permission.web.reactive;

import club.p6e.coat.common.error.PermissionException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.validator.PermissionValidator;
import jakarta.annotation.Nonnull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission Filter
 *
 * @author lidashuang
 * @version 1.0
 */
public class PermissionFilter implements WebFilter, Ordered {

    @SuppressWarnings("ALL")
    private static final String PROJECT_HEADER = "P6e-Project";

    @SuppressWarnings("ALL")
    private static final String PERMISSION_HEADER = "P6e-Permission";

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
    public PermissionFilter(PermissionValidator validator) {
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
                    "filter(ServerWebExchange exchange, WebFilterChain chain).",
                    "request permission exception."
            ));
        } else {
            exchange.mutate().request(request.mutate().header(
                    PERMISSION_HEADER, JsonUtil.toJson(details)).build()).build();
            return chain.filter(exchange);
        }
    }

    public PermissionDetails validate(ServerHttpRequest request) {
        final List<String> permissions = new ArrayList<>();
        final String path = request.getPath().value();
        final String method = request.getMethod().name().toUpperCase();
        final String project = request.getHeaders().getFirst(PROJECT_HEADER);
        final List<String> list = request.getHeaders().get(USER_PERMISSION_HEADER);
        if (list != null) {
            for (final String item : list) {
                final List<String> data = JsonUtil.fromJsonToList(item, String.class);
                if (data != null) {
                    permissions.addAll(data);
                }
            }
        }
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxx");
        final PermissionDetails details;
        if (project == null || project.isEmpty()) {
            System.out.println("ttttttttt  permissions >>> " + permissions);
            details = validator.execute(path, method, permissions);
        } else {
            details = validator.execute(path, method, project, permissions);
        }
        return details;
    }

}
