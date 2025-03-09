package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthCertificateValidator;
import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.cache.QrCodeLoginCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 二维码回调服务的默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class QrCodeCallbackServiceImpl implements QrCodeCallbackService {

    /**
     * 用户信息的请求头名称
     */
    @SuppressWarnings("ALL")
    private static final String USER_HEADER_NAME = "P6e-User-Info";

    /**
     * 认证用户
     */
    private final AuthUser<?> au;

    /**
     * 二维码登录缓存
     */
    private final QrCodeLoginCache cache;

    /**
     * 认证证书验证器
     */
    private final AuthCertificateValidator validator;


    /**
     * 构造方法初始化
     *
     * @param au        认证用户
     * @param cache     二维码登录缓存
     * @param validator 认证证书验证器
     */
    public QrCodeCallbackServiceImpl(
            AuthUser<?> au,
            QrCodeLoginCache cache,
            AuthCertificateValidator validator
    ) {
        this.au = au;
        this.cache = cache;
        this.validator = validator;
    }

    @Override
    public Mono<LoginContext.QrCodeCallback.Dto> execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param) {
        final String content = param.getContent();
        return validator
                .execute(exchange)
                .flatMap(e -> {
                    final ServerHttpRequest request = e.getRequest();
                    final HttpHeaders httpHeaders = request.getHeaders();
                    final List<String> list = httpHeaders.get(USER_HEADER_NAME);
                    if (list != null && !list.isEmpty()) {
                        return au.create(list.get(0));
                    } else {
                        return Mono.error(GlobalExceptionContext.exceptionAuthException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param)",
                                "QrCode callback login auth cache data does not exist or expire exception."
                        ));
                    }
                })
                .flatMap(u -> cache
                        .get(content)
                        .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param)",
                                "QrCode callback login cache data does not exist or expire exception."
                        )))
                        .flatMap(s -> {
                            if (QrCodeLoginCache.isEmpty(s)) {
                                return cache.set(content, u.id());
                            } else {
                                return Mono.error(GlobalExceptionContext.executeCacheException(
                                        this.getClass(),
                                        "fun execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param)",
                                        "QrCode callback login cache other data exists exception."
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
