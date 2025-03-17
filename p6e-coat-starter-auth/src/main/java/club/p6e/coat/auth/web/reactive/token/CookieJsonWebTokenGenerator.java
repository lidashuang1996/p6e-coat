package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Cookie Json Web Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public class CookieJsonWebTokenGenerator implements TokenGenerator {

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
    public CookieJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final Properties.Token properties = Properties.getInstance().getToken();
        final long expiration = properties.getDuration().getSeconds();
        final String device = ((ServerHttpRequest) exchange.getRequest()).getDevice();
        final String content = codec.encryption(user.id(), (device == null ? "PC" : device) + "@" + user.serialize(), expiration);
        return Mono.just(content).flatMap(c -> Mono.just(ResponseCookie.from(AUTH_COOKIE_NAME, c).maxAge(expiration).secure(true).build()));
    }

}
