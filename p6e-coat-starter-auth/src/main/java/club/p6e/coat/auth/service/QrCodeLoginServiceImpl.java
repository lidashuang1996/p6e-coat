package club.p6e.coat.auth.service;

import club.p6e.coat.auth.cache.QrCodeLoginCache;
import club.p6e.coat.auth.repository.WebFluxUserRepository;
import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 二维码登录服务的默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class QrCodeLoginServiceImpl implements QrCodeLoginService {

    /**
     * 认证用户
     */
    private final AuthUser<?> au;

    /**
     * 二维码缓存对象
     */
    private final QrCodeLoginCache cache;

    /**
     * 用户存储库
     */
    private final WebFluxUserRepository repository;

    /**
     * 构造方法初始化
     *
     * @param au         认证用户
     * @param cache      二维码缓存对象
     * @param repository 用户存储库
     */
    public QrCodeLoginServiceImpl(
            AuthUser<?> au,
            QrCodeLoginCache cache,
            WebFluxUserRepository repository) {
        this.au = au;
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<AuthUser.Model> execute(ServerWebExchange exchange, LoginContext.QrCode.Request param) {
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> {
                    final String mark = v.getQuickResponseCodeLoginMark();
                    return cache
                            .get(mark)
                            .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                                    this.getClass(),
                                    "fun execute(ServerWebExchange exchange, LoginContext.QrCode.Request param).",
                                    "QrCode login cache data does not exist or expire exception."
                            )))
                            .flatMap(s -> {
                                if (QrCodeLoginCache.isEmpty(s)) {
                                    return Mono.error(GlobalExceptionContext.executeQrCodeDataNullException(
                                            this.getClass(),
                                            "fun execute(ServerWebExchange exchange, LoginContext.QrCode.Request param).",
                                            "QrCode login data is null exception."
                                    ));
                                } else {
                                    return cache
                                            .del(mark)
                                            .flatMap(l -> repository
                                                    .findById(Integer.valueOf(s))
                                                    .flatMap(m -> {
                                                        if (m == null) {
                                                            return Mono.error(GlobalExceptionContext.executeUserNotExistException(
                                                                    this.getClass(),
                                                                    "fun execute(ServerWebExchange exchange, LoginContext.QrCode.Request param)",
                                                                    "QrCode login user id select data does not exist exception."
                                                            ));
                                                        } else {
                                                            return au.create(m, null);
                                                        }
                                                    }));
                                }
                            });
                });
    }
}
