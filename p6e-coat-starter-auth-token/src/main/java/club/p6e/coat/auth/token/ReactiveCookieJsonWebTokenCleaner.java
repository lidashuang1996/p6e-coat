package club.p6e.coat.auth.token;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive Cookie JSON Web Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
public class ReactiveCookieJsonWebTokenCleaner implements ReactiveTokenCleaner {

    /**
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * JSON Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec JSON Web Token Codec Object
     */
    public ReactiveCookieJsonWebTokenCleaner(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final String key : cookies.keySet()) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(key)) {
                    response.addCookie(cookie(key, ""));
                }
            }
        }
        return Mono.just(LocalDateTime.now());
    }

    /**
     * Cookie
     *
     * @param name    Cookie Name
     * @param content Cookie Content
     * @return Response Cookie Object
     */
    public ResponseCookie cookie(String name, String content) {
        return ResponseCookie.from(name, content).path("/").maxAge(0).httpOnly(true).build();
    }

}
