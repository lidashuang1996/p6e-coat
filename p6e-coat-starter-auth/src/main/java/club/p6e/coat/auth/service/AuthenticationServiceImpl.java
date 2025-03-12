package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.TokenValidator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     */
    public AuthenticationServiceImpl(TokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param) {
        return validator.execute(exchange);
    }

}
