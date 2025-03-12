package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.web.reactive.cache.QuickResponseCodeLoginCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class QuickResponseCodeAcquisitionServiceImpl implements QuickResponseCodeAcquisitionService {

    /**
     * Quick Response Code Login Cache Object
     */
    private final QuickResponseCodeLoginCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Quick Response Code Login Cache Object
     */
    public QuickResponseCodeAcquisitionServiceImpl(QuickResponseCodeLoginCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<LoginContext.QuickResponseCodeObtain.Dto> execute(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeObtain.Request param) {
        final ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        final String code = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        request.setQuickResponseCodeLoginMark(code);
        return cache
                .set(code, QuickResponseCodeLoginCache.EMPTY_CONTENT)
                .flatMap(b -> b ? Mono.just(new LoginContext.QuickResponseCodeObtain.Dto().setContent(code))
                        : Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QrCodeObtain.Request param)",
                        "QrCode obtain login cache data write exception."
                )));
    }

}
