package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.ReactiveTokenValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Authentication Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.ReactiveLoginAuthenticationServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginAuthenticationServiceImpl implements ReactiveLoginAuthenticationService {

    /**
     * Reactive Token Validator Object
     */
    private final ReactiveTokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Reactive Token Validator Object
     */
    public ReactiveLoginAuthenticationServiceImpl(ReactiveTokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param) {
        return validator.execute(exchange);
    }

}
