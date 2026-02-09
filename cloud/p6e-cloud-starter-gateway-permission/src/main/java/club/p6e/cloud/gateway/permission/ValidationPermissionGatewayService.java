package club.p6e.cloud.gateway.permission;

import club.p6e.coat.common.exception.PermissionException;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.filter.ReactivePermissionFilter;
import club.p6e.coat.permission.validator.PermissionValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validation Permission Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(ValidationPermissionGatewayService.class)
public class ValidationPermissionGatewayService {

    /**
     * Reactive Permission Filter Object
     */
    private final ReactivePermissionFilter filter;

    /**
     * Constructor Initialization
     *
     * @param validator Permission Validator Object
     */
    public ValidationPermissionGatewayService(PermissionValidator validator) {
        this.filter = new ReactivePermissionFilter(validator);
    }

    /**
     * Execute Permission Service
     *
     * @param exchange Server Web Exchange Object
     * @return Mono<PermissionDetails> Permission Details Object
     */
    public Mono<PermissionDetails> execute(ServerWebExchange exchange) {
        final PermissionDetails details = filter.validate(exchange.getRequest());
        if (details == null) {
            return Mono.error(new PermissionException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange)",
                    "request permission exception"
            ));
        } else {
            return Mono.just(details);
        }
    }

}
