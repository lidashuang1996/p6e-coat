package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.ReactiveTokenValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = AuthenticationLoginService.class,
        ignored = AuthenticationLoginServiceImpl.class
)
public class AuthenticationLoginServiceImpl implements AuthenticationLoginService {

    /**
     * Token Validator Object
     */
    private final ReactiveTokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     */
    public AuthenticationLoginServiceImpl(ReactiveTokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param) {
        return validator.execute(exchange);
    }

}
