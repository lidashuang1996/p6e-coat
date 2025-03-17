package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Local Storage Json Web Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public class LocalStorageJsonWebTokenGenerator implements TokenGenerator {

    /**
     * Json Web Token Codec Object
     */
    private final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public LocalStorageJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final String device = ((ServerHttpRequest) exchange.getRequest()).getDevice();
        final Properties.Token properties = Properties.getInstance().getToken();
        final long expiration = properties.getDuration().getSeconds();
        final String content = codec.encryption(user.id(), (device == null ? "PC" : device) + "@" + user.serialize(), expiration);
        return Mono.just(content).map(m -> new HashMap<>() {{
            put("token", content);
            put("type", "Bearer");
            put("expiration", expiration);
        }});
    }

}
