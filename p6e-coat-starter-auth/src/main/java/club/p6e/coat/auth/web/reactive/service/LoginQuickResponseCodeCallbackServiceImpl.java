package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.aspect.VoucherAspect;
import club.p6e.coat.auth.web.reactive.cache.LoginQuickResponseCodeCache;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
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
@Component
@ConditionalOnMissingBean(
        value = LoginQuickResponseCodeCallbackService.class,
        ignored = LoginQuickResponseCodeCallbackServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class LoginQuickResponseCodeCallbackServiceImpl implements LoginQuickResponseCodeCallbackService {

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Login Quick Response Code Cache Object
     */
    private final LoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache      Login Quick Response Code Cache Object
     * @param repository User Repository Object
     */
    public LoginQuickResponseCodeCallbackServiceImpl(LoginQuickResponseCodeCache cache, UserRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param) {
        final String mark = TransformationUtil.objectToString(exchange.getRequest()
                .getAttributes().get(VoucherAspect.MyServerHttpRequestDecorator.QUICK_RESPONSE_CODE_LOGIN_MARK));
        return cache
                .get(mark)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param)",
                        "login quick response code cache data does not exist or expire exception"
                )))
                .flatMap(s -> {
                    if (LoginQuickResponseCodeCache.isEmpty(s)) {
                        return Mono.error(GlobalExceptionContext.executeQrCodeDataNullException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param)",
                                "login quick response code data is null exception"
                        ));
                    } else {
                        return cache.del(mark).flatMap(l -> repository.findById(Integer.valueOf(s))
                                .flatMap(u -> u == null ? Mono.error(GlobalExceptionContext.executeUserNoExistException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param)",
                                        "login quick response code user id select data does not exist exception"
                                )) : Mono.just(u)));
                    }
                });
    }
}
