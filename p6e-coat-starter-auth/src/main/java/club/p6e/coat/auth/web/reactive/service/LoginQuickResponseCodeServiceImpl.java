package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.token.web.reactive.TokenValidator;
import club.p6e.coat.auth.web.reactive.cache.LoginQuickResponseCodeCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Quick Response Code Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = LoginQuickResponseCodeService.class,
        ignored = LoginQuickResponseCodeServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class LoginQuickResponseCodeServiceImpl implements LoginQuickResponseCodeService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Login Quick Response Code Cache Object
     */
    private final LoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     * @param cache     Login Quick Response Code Cache Object
     */
    public LoginQuickResponseCodeServiceImpl(TokenValidator validator, LoginQuickResponseCodeCache cache) {
        this.cache = cache;
        this.validator = validator;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param) {
        final String content = param.getContent();
        return validator
                .execute(exchange)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                        "login quick response code auth exception"
                )))
                .flatMap(u -> cache.get(content)
                        .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                                "login quick response code cache data does not exist or expire exception"
                        )))
                        .flatMap(s -> {
                            if (LoginQuickResponseCodeCache.isEmpty(s)) {
                                return cache.set(content, u.id()).switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                                        "login quick response code cache write exception"
                                )));
                            } else {
                                return Mono.just(Mono.error(GlobalExceptionContext.executeCacheException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                                        "login quick response code cache data exist other user exception"
                                )));
                            }
                        }).map(r -> u));
    }

}
