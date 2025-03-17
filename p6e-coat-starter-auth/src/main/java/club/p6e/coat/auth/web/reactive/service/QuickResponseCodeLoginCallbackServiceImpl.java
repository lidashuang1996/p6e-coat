package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.web.reactive.cache.QuickResponseCodeLoginCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.token.TokenValidator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Callback Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class QuickResponseCodeLoginCallbackServiceImpl implements QuickResponseCodeLoginCallbackService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Quick Response Code Login Cache Object
     */
    private final QuickResponseCodeLoginCache cache;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     * @param cache     Quick Response Code Login Cache Object
     */
    public QuickResponseCodeLoginCallbackServiceImpl(TokenValidator validator, QuickResponseCodeLoginCache cache) {
        this.cache = cache;
        this.validator = validator;
    }

    @Override
    public Mono<LoginContext.QuickResponseCodeCallback.Dto> execute(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param) {
        final String content = param.getContent();
        System.out.println("Content: " + content);
        return validator
                .execute(exchange)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                        "quick response code callback login auth exception."
                )))
                .flatMap(u -> cache.get(content).switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                        "quick response code callback login cache data does not exist or expire exception."
                ))).flatMap(s -> {
                    System.out.println("sssss >>> " + s);
                    if (QuickResponseCodeLoginCache.isEmpty(s)) {
                        return cache.set(content, u.id()).map(b ->
                                new LoginContext.QuickResponseCodeCallback.Dto().setContent("SUCCESS"));
                    } else {
                        return Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param)",
                                "quick response code callback login cache other data exists exception."
                        ));
                    }
                }));
    }
    
}
