package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Reactive Local Storage Json Web Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveLocalStorageJsonWebTokenGenerator implements ReactiveTokenGenerator {

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public ReactiveLocalStorageJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final int duration = duration();
        final String device = device(exchange);
        final String content = this.codec.encryption(user.id(), (device == null ? "PC" : device) + "@" + user.serialize(), duration);
        return Mono.just(content).map(m -> new HashMap<>() {{
            put("token", content);
            put("type", "Bearer");
            put("expiration", duration);
        }});
    }

    /**
     * Get Device Content
     *
     * @param exchange Server Web Exchange Object
     * @return Device Content
     */
    public String device(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("P6e-Device");
    }

    /**
     * Get Cookie Duration
     *
     * @return Cookie Duration
     */
    public int duration() {
        return 3600;
    }

}
