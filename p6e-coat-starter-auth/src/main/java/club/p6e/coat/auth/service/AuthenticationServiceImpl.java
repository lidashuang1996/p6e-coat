package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.TokenValidator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    public AuthenticationServiceImpl(TokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public Mono<AuthUser.Model> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param) {
        return validator
                .execute(exchange)
                .flatMap();
    }

}
