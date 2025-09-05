package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.web.reactive.TokenValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Authentication Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = LoginAuthenticationService.class,
        ignored = LoginAuthenticationServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class LoginAuthenticationServiceImpl implements LoginAuthenticationService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     */
    public LoginAuthenticationServiceImpl(TokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param) {
        return validator.execute(exchange);
    }

}
