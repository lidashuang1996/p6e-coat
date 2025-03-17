package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.web.reactive.cache.QuickResponseCodeLoginCache;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class QuickResponseCodeLoginCallbackServiceImpl implements QuickResponseCodeLoginCallbackService {

    /**
     * 二维码缓存对象
     */
    private final QuickResponseCodeLoginCache cache;

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Constructor Initialization
     *
     * @param cache      二维码缓存对象
     * @param repository User Repository Object
     */
    public QuickResponseCodeLoginCallbackServiceImpl(QuickResponseCodeLoginCache cache, UserRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param) {
        final ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        final String mark = request.getQuickResponseCodeLoginMark();
        return cache
                .get(mark)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param).",
                        "quick response code login cache data does not exist or expire exception."
                )))
                .flatMap(s -> {
                    if (QuickResponseCodeLoginCache.isEmpty(s)) {
                        return Mono.error(GlobalExceptionContext.executeQrCodeDataNullException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param).",
                                "quick response code login data is null exception."
                        ));
                    } else {
                        return cache.del(mark).flatMap(l -> repository.findById(Integer.valueOf(s))
                                .flatMap(u -> u == null ? Mono.error(GlobalExceptionContext.executeUserNotExistException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param).",
                                        "quick response code login user id select data does not exist exception."
                                )) : Mono.just(u)));
                    }
                });
    }
}
