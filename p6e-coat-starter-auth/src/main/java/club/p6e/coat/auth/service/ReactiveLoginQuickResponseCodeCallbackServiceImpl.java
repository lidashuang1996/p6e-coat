package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.cache.ReactiveLoginQuickResponseCodeCache;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.exception.AuthException;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.exception.QuickResponseCodeDataNullException;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Quick Response Code Callback Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveLoginQuickResponseCodeCallbackService.class,
        ignored = ReactiveLoginQuickResponseCodeCallbackServiceImpl.class
)
@Component("club.p6e.coat.auth.service.ReactiveLoginQuickResponseCodeCallbackServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginQuickResponseCodeCallbackServiceImpl implements ReactiveLoginQuickResponseCodeCallbackService {

    /**
     * Reactive User Repository Object
     */
    private final ReactiveUserRepository repository;

    /**
     * Reactive Login Quick Response Code Cache Object
     */
    private final ReactiveLoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository Reactive User Repository Object
     * @param cache      Reactive Login Quick Response Code Cache Object
     */
    public ReactiveLoginQuickResponseCodeCallbackServiceImpl(ReactiveUserRepository repository, ReactiveLoginQuickResponseCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param) {
        final String mark = TransformationUtil.objectToString(exchange.getRequest()
                .getAttributes().get(ReactiveVoucherAspect.MyServerHttpRequestDecorator.QUICK_RESPONSE_CODE_LOGIN_MARK));
        return cache
                .get(mark)
                .switchIfEmpty(Mono.error(new CacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param)",
                        "login quick response code cache data does not exist or expire exception"
                )))
                .flatMap(s -> {
                    if (ReactiveLoginQuickResponseCodeCache.isEmpty(s)) {
                        return Mono.error(new QuickResponseCodeDataNullException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param)",
                                "login quick response code data is null exception"
                        ));
                    } else {
                        return cache.del(mark).flatMap(l -> repository.findById(Integer.valueOf(s))
                                .flatMap(u -> u == null ? Mono.error(new AuthException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param)",
                                        "login quick response code user id select data does not exist exception"
                                )) : Mono.just(u)));
                    }
                });
    }
}
