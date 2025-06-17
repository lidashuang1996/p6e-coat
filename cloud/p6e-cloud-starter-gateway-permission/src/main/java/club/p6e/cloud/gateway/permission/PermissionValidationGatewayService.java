package club.p6e.cloud.gateway.permission;

import club.p6e.coat.common.error.PermissionException;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.validator.PermissionValidator;
import club.p6e.coat.permission.web.reactive.PermissionFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Permission Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(PermissionValidationGatewayService.class)
public class PermissionValidationGatewayService {

    /**
     * Permission Filter Object
     */
    private final PermissionFilter filter;

    /**
     * Constructor Initialization
     *
     * @param validator Permission Validator Object
     */
    public PermissionValidationGatewayService(PermissionValidator validator) {
        this.filter = new PermissionFilter(validator);
    }

    /**
     * Execute Permission Service
     *
     * @param exchange Server Web Exchange Object
     * @return Mono<ServerWebExchange> Server Web Exchange Object
     */
    public Mono<PermissionDetails> execute(ServerWebExchange exchange) {
        final PermissionDetails details = filter.validate(exchange.getRequest());
        if (details == null) {
            return Mono.error(new PermissionException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange).",
                    "request permission exception."
            ));
        } else {
            return Mono.just(details);
        }
    }

}
