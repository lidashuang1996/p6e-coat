package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.token.JsonWebTokenCodec;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Cookie Json Web Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CookieJsonWebTokenValidator implements TokenValidator {

    /**
     * Auth Cookie Name
     */
    private static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * Json Web Token Codec Object
     */
    private final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public CookieJsonWebTokenValidator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final String key : cookies.keySet()) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(key)) {
                    for (final HttpCookie cookie : cookies.get(key)) {
                        String content = codec.decryption(cookie.getValue());
                        if (content != null) {
                            return Mono.just(SpringUtil.getBean(UserBuilder.class).create(content));
                        }
                    }
                }
            }
        }
        return Mono.empty();
    }

}
