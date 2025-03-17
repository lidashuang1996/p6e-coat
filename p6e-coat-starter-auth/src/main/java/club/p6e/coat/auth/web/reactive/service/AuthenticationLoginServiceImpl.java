package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.token.TokenValidator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthenticationLoginServiceImpl implements AuthenticationLoginService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     */
    public AuthenticationLoginServiceImpl(TokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param) {
        return validator.execute(exchange);
    }

}
