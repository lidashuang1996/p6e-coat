package club.p6e.coat.auth.certificate;

import club.p6e.coat.auth.AuthCertificateAuthority;
import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * [HTTP/STORAGE/JWT] CertificateAuthority
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class HttpLocalStorageJsonWebTokenCertificateAuthority extends HttpCertificate implements AuthCertificateAuthority {

    /**
     * JWT 密码对象
     */
    protected final JsonWebTokenCodec cipher;

    /**
     * 构造方法初始化
     *
     * @param cipher JWT 密码对象
     */
    public HttpLocalStorageJsonWebTokenCertificateAuthority(JsonWebTokenCodec cipher) {
        this.cipher = cipher;
    }

    @Override
    public Mono<ResultContext> award(ServerWebExchange exchange, AuthUser.Model model) {
        final String uid = model.id();
        final String info = model.serialize();
        final String accessToken = jwtEncryption(uid, info, cipher.getAccessTokenSecret());
        final String refreshToken = jwtEncryption(uid, info, cipher.getRefreshTokenSecret());
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> {
                    if (v.isOAuth2()) {
                        return v.setOAuth2User(uid, info).flatMap(vv ->
                                setHttpLocalStorageToken(accessToken, refreshToken,
                                        new HashMap<>() {{
                                            put("oauth2", v.isOAuth2());
                                        }}
                                ));
                    } else {
                        return v.del().flatMap(vv -> setHttpLocalStorageToken(accessToken, refreshToken));
                    }
                }).map(ResultContext::build);
    }

    @Override
    public Mono<Void> abolish(ServerWebExchange exchange) {
        return getHttpLocalStorageToken(exchange.getRequest())
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAuthException(
                        this.getClass(),
                        "fun abolish(ServerWebExchange exchange).",
                        "[HTTP/STORAGE/JWT] HTTP request access token does not exist."
                )))
                .flatMap(l -> Mono.empty());
    }

}
