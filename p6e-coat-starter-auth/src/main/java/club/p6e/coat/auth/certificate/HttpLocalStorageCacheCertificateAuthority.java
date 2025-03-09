package club.p6e.coat.auth.certificate;

import club.p6e.coat.auth.AuthCertificateAuthority;
import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.cache.UserTokenCache;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.AuthAccessTokenGenerator;
import club.p6e.coat.auth.generator.AuthRefreshTokenGenerator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * [ HTTP/STORAGE/CACHE ] CacheCertificateAuthority
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class HttpLocalStorageCacheCertificateAuthority extends HttpCertificate implements AuthCertificateAuthority {

    /**
     * 认证缓存
     */
    protected final UserTokenCache cache;

    /**
     * ACCESS TOKEN 生成器
     */
    protected final AuthAccessTokenGenerator accessTokenGenerator;

    /**
     * REFRESH TOKEN 生成器
     */
    protected final AuthRefreshTokenGenerator refreshTokenGenerator;

    /**
     * 构造方法初始化
     *
     * @param accessTokenGenerator  ACCESS TOKEN 生成器
     * @param refreshTokenGenerator REFRESH TOKEN 生成器
     */
    public HttpLocalStorageCacheCertificateAuthority(
            UserTokenCache cache,
            AuthAccessTokenGenerator accessTokenGenerator,
            AuthRefreshTokenGenerator refreshTokenGenerator
    ) {
        this.cache = cache;
        this.accessTokenGenerator = accessTokenGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
    }

    @Override
    public Mono<ResultContext> award(ServerWebExchange exchange, AuthUser.Model model) {
        final String uid = model.id();
        final String info = model.serialize();
        final String accessToken = accessTokenGenerator.execute();
        final String refreshToken = refreshTokenGenerator.execute();
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> cache
                        .set(uid, v.device(), accessToken, refreshToken, info)
                        .flatMap(t -> {
                            if (v.isOAuth2()) {
                                return v.setOAuth2User(uid, info).flatMap(vv -> setHttpLocalStorageToken(
                                        t.getAccessToken(), t.getRefreshToken(), new HashMap<>() {{
                                            put("oauth2", v.isOAuth2());
                                        }}
                                ));
                            } else {
                                return v.del().flatMap(vv -> setHttpLocalStorageToken(t.getAccessToken(), t.getRefreshToken()));
                            }
                        })
                ).map(ResultContext::build);
    }

    @Override
    public Mono<Void> abolish(ServerWebExchange exchange) {
        return getHttpLocalStorageToken(exchange.getRequest())
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun abolish(ServerWebExchange exchange).",
                        "[HTTP/STORAGE/CACHE] HTTP request access token does not exist."
                )))
                .flatMap(cache::getAccessToken)
                .flatMap(t -> cache.cleanToken(t.getAccessToken()))
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun abolish(ServerWebExchange exchange).",
                        "[HTTP/STORAGE/CACHE] Verifier validation clean access token exception."
                )))
                .flatMap(l -> Mono.empty());
    }

}
