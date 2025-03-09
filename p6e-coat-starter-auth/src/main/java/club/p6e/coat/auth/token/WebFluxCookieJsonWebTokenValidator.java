package club.p6e.coat.auth.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.User;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxCookieJsonWebTokenValidator implements WebFluxTokenValidator {

    private final JsonWebTokenCodec codec;

    public WebFluxCookieJsonWebTokenValidator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final String key : cookies.keySet()) {
                if ("P6e-User-Auth".equalsIgnoreCase(key)) {
                    String content = null;
                    for (final HttpCookie cookie : cookies.get(key)) {
                        content = codec.decryption(cookie.getValue());
                        if (content != null) {
                            break;
                        }
                    }
                    return Mono.just(User.create(content));
                }
            }
        }
        return Mono.empty();
    }


}
