package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Cookie Json Web Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveCookieJsonWebTokenValidator implements ReactiveTokenValidator {

    /**
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * User Builder Object
     */
    protected final UserBuilder builder;

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param builder User Builder Object
     * @param codec   Json Web Token Codec Object
     */
    public ReactiveCookieJsonWebTokenValidator(UserBuilder builder, JsonWebTokenCodec codec) {
        this.codec = codec;
        this.builder = builder;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final String key : cookies.keySet()) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(key)) {
                    for (final HttpCookie cookie : cookies.get(key)) {
                        String content = this.codec.decryption(cookie.getValue());
                        if (content != null) {
                            content = content.substring(content.indexOf("@") + 1);
                            return Mono.just(this.builder.create(content));
                        }
                    }
                }
            }
        }
        return Mono.empty();
    }

}
