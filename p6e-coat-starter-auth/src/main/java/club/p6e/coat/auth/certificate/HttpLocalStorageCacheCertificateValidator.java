package club.p6e.coat.auth.certificate;

import club.p6e.coat.auth.AuthCertificateValidator;
import club.p6e.coat.auth.cache.UserTokenCache;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * [ HTTP/STORAGE/CACHE ] CertificateValidator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class HttpLocalStorageCacheCertificateValidator extends HttpCertificate implements AuthCertificateValidator {

    /**
     * 认证缓存的对象
     */
    protected final UserTokenCache cache;

    /**
     * 构造方法初始化
     *
     * @param cache 认证缓存的对象
     */
    public HttpLocalStorageCacheCertificateValidator(UserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<ServerWebExchange> execute(ServerWebExchange exchange) {
        return getHttpLocalStorageToken(exchange.getRequest())
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange)",
                        "[HTTP/STORAGE/CACHE] HTTP request access token does not exist."
                )))
                .flatMap(this::accessToken)
                .map(s -> exchange.mutate().request(
                        exchange.getRequest().mutate().header(getUserInfoHeaderName(), s).build()
                ).build());
    }

    @Override
    public Mono<String> accessToken(String token) {
        return cache.getAccessToken(token)
                .flatMap(t -> cache.getUser(t.getUid()))
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun accessToken(String token).",
                        "[HTTP/STORAGE/CACHE] Verifier validation access token exception."
                )));
    }

    @Override
    public Mono<String> refreshToken(String token) {
        return cache.getRefreshToken(token)
                .flatMap(t -> cache.getUser(t.getUid()))
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun refreshToken(String token).",
                        "[HTTP/STORAGE/CACHE] Verifier validation refresh token exception."
                )));
    }
}
