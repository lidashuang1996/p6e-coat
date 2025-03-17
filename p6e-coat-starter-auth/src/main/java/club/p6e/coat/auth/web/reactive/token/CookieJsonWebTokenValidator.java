package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class CookieJsonWebTokenValidator implements TokenValidator {

    private final JsonWebTokenCodec codec;

    public CookieJsonWebTokenValidator(JsonWebTokenCodec codec) {
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
                    return Mono.just(SpringUtil.getBean(UserBuilder.class).create(content));
                }
            }
        }
        return Mono.empty();
    }


}
