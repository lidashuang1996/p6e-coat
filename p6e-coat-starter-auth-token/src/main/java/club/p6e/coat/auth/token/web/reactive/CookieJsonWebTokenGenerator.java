package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.token.JsonWebTokenCodec;
import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Cookie Json Web Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CookieJsonWebTokenGenerator implements TokenGenerator {

    /**
     * Auth Cookie Name
     */
    private static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * Device Header Name
     */
    @SuppressWarnings("ALL")
    private static final String DEVICE_HEADER_NAME = "P6e-Device";

    /**
     * Json Web Token Codec Object
     */
    private final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public CookieJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final long duration = duration();
        final String device = exchange.getRequest().getHeaders().getFirst(DEVICE_HEADER_NAME);
        final String content = codec.encryption(user.id(), (device == null ? "PC" : device) + "@" + user.serialize(), duration);
        return Mono.just(content).flatMap(c -> Mono.just(ResponseCookie.from(AUTH_COOKIE_NAME, c).maxAge(duration).secure(true).build()));
    }

    /**
     * Cache Duration
     *
     * @return Cache Duration Number
     */
    public long duration() {
        return 3600L;
    }

}
