package club.p6e.coat.auth.certificate;

import club.p6e.coat.auth.AuthCertificateValidator;
import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * [ HTTP/COOKIE/JWT ] CertificateValidator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class HttpCookieJsonWebTokenCertificateValidator extends HttpCertificate implements AuthCertificateValidator {

    /**
     * JWT 密码对象
     */
    protected final JsonWebTokenCodec cipher;

    /**
     * 构造方法初始化
     *
     * @param cipher JWT 密码对象
     */
    public HttpCookieJsonWebTokenCertificateValidator(JsonWebTokenCodec cipher) {
        this.cipher = cipher;
    }

    @Override
    public Mono<ServerWebExchange> execute(ServerWebExchange exchange) {
        return getHttpCookieToken(exchange.getRequest())
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange).",
                        "[HTTP/COOKIE/JWT] HTTP request access token does not exist."
                )))
                .flatMap(this::accessToken)
                .map(s -> exchange.mutate().request(
                        exchange.getRequest().mutate().header(getUserInfoHeaderName(), s).build()
                ).build());
    }

    @Override
    public Mono<String> accessToken(String token) {
        final String r = jwtDecryption(token, cipher.getAccessTokenSecret());
        return r == null ? Mono.error(GlobalExceptionContext.exceptionAuthException(
                this.getClass(),
                "fun accessToken(String token).",
                "[HTTP/COOKIE/JWT] Verifier validation access token exception."
        )) : Mono.just(r);
    }

    @Override
    public Mono<String> refreshToken(String token) {
        final String r = jwtDecryption(token, cipher.getRefreshTokenSecret());
        return r == null ? Mono.error(GlobalExceptionContext.exceptionAuthException(
                this.getClass(),
                "fun refreshToken(String token)",
                "[HTTP/COOKIE/JWT] Verifier validation refresh token exception."
        )) : Mono.just(r);
    }

}
