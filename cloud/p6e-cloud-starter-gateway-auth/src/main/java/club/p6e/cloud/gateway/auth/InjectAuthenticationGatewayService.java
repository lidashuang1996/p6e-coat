package club.p6e.cloud.gateway.auth;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.token.ReactiveTokenValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Inject Authentication Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(InjectAuthenticationGatewayService.class)
public class InjectAuthenticationGatewayService {

    /**
     * Reactive Token Validator Object
     */
    private final ReactiveTokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Reactive Token Validator Object
     */
    public InjectAuthenticationGatewayService(ReactiveTokenValidator validator) {
        this.validator = validator;
    }

    /**
     * Execute
     *
     * @param exchange Server Web Exchange Object
     * @return Mono<User> User Object
     */
    public Mono<User> execute(ServerWebExchange exchange) {
        return validator.execute(exchange);
    }

}
