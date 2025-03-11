package club.p6e.coat.auth.service;

import club.p6e.coat.auth.cache.QuickResponseCodeLoginCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.token.TokenValidator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Callback Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class QuickResponseCodeCallbackServiceImpl implements QuickResponseCodeCallbackService {

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
    public QuickResponseCodeCallbackServiceImpl(TokenValidator validator, QuickResponseCodeLoginCache cache) {
        this.cache = cache;
        this.validator = validator;
    }

    @Override
    public Mono<LoginContext.QrCodeCallback.Dto> execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param) {
        final String content = param.getContent();

        return validator
                .execute(exchange)
                .flatMap(u -> cache.get(content).switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param)",
                        "quick response code callback login cache data does not exist or expire exception."
                ))).flatMap(s -> {
                    if (QuickResponseCodeLoginCache.isEmpty(s)) {
                        return cache.set(content, u.id());
                    } else {
                        return Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param)",
                                "quick response code callback login cache other data exists exception."
                        ));
                    }
                }))
                .flatMap(b -> {
                    if (b) {
                        return Mono.just(new LoginContext.QrCodeCallback.Dto().setContent("SUCCESS"));
                    } else {
                        return Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param)",
                                "QrCode callback login cache write exception."
                        ));
                    }
                });
    }
}
