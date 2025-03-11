package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.cache.QuickResponseCodeLoginCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.QrCodeLoginGenerator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 二维码获取服务默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class QrCodeObtainServiceImpl implements QrCodeObtainService {

    /**
     * 二维码缓存对象
     */
    private final QuickResponseCodeLoginCache cache;

    /**
     * 二维码生成器对象
     */
    private final QrCodeLoginGenerator generator;

    /**
     * Constructor Initialization
     *
     * @param cache     二维码缓存对象
     * @param generator 二维码生成器对象
     */
    public QrCodeObtainServiceImpl(
            QuickResponseCodeLoginCache cache,
            QrCodeLoginGenerator generator
    ) {
        this.cache = cache;
        this.generator = generator;
    }

    @Override
    public Mono<LoginContext.QrCodeObtain.Dto> execute(ServerWebExchange exchange, LoginContext.QrCodeObtain.Request param) {
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> {
                    final String code = generator.execute();
                    return cache
                            .set(code, QuickResponseCodeLoginCache.EMPTY_CONTENT)
                            .flatMap(b -> {
                                if (b) {
                                    return v.setQuickResponseCodeLoginData(code)
                                            .map(vv -> new LoginContext.QrCodeObtain.Dto().setContent(code));
                                } else {
                                    return Mono.error(
                                            GlobalExceptionContext.executeCacheException(
                                                    this.getClass(),
                                                    "fun execute(ServerWebExchange exchange, LoginContext.QrCodeObtain.Request param)",
                                                    "QrCode obtain login cache data write exception."
                                            ));
                                }
                            });
                });
    }

}
