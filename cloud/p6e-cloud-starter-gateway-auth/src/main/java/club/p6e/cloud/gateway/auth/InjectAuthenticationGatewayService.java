package club.p6e.cloud.gateway.auth;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.token.web.reactive.TokenValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(InjectAuthenticationGatewayService.class)
public class InjectAuthenticationGatewayService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     */
    public InjectAuthenticationGatewayService(TokenValidator validator) {
        this.validator = validator;
    }

    /**
     * Execute Authentication Service
     *
     * @param exchange Server Web Exchange Object
     * @return Server Web Exchange Object
     */
    public Mono<User> execute(ServerWebExchange exchange) {
        return validator.execute(exchange);
    }

}
