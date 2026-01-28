package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.ReactiveTokenValidator;
import club.p6e.coat.auth.cache.ReactiveLoginQuickResponseCodeCache;
import club.p6e.coat.common.error.AuthException;
import club.p6e.coat.common.error.CacheException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Quick Response Code Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveLoginQuickResponseCodeService.class,
        ignored = ReactiveLoginQuickResponseCodeServiceImpl.class
)
@Component("club.p6e.coat.auth.service.ReactiveLoginQuickResponseCodeServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginQuickResponseCodeServiceImpl implements ReactiveLoginQuickResponseCodeService {

    /**
     * Reactive Token Validator Object
     */
    private final ReactiveTokenValidator validator;

    /**
     * Reactive Login Quick Response Code Cache Object
     */
    private final ReactiveLoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param validator Reactive Token Validator Object
     * @param cache     Reactive Login Quick Response Code Cache Object
     */
    public ReactiveLoginQuickResponseCodeServiceImpl(ReactiveTokenValidator validator, ReactiveLoginQuickResponseCodeCache cache) {
        this.cache = cache;
        this.validator = validator;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param) {
        final String content = param.getContent();
        return validator
                .execute(exchange)
                .switchIfEmpty(Mono.error(new AuthException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                        "login quick response code auth exception"
                )))
                .flatMap(u -> cache.get(content)
                        .switchIfEmpty(Mono.error(new CacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                                "login quick response code cache data does not exist or expire exception"
                        )))
                        .flatMap(s -> {
                            if (ReactiveLoginQuickResponseCodeCache.isEmpty(s)) {
                                return cache.set(content, u.id()).switchIfEmpty(Mono.error(new CacheException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                                        "login quick response code cache write exception"
                                )));
                            } else {
                                return Mono.just(Mono.error(new CacheException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                                        "login quick response code cache data exist other user exception"
                                )));
                            }
                        }).map(r -> u));
    }

}
