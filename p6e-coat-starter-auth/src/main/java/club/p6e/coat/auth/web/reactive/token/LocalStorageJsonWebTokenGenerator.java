package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
public class LocalStorageJsonWebTokenGenerator implements TokenGenerator {

    private final JsonWebTokenCodec codec;

    public LocalStorageJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange context, User user) {
        final String device = context.getAttribute("P6e-Device");
        final String content = codec.encryption(user.id(), device, user.serialize());
        return Mono.just(content).map(m -> new HashMap<>() {{
            put("token", content);
            put("expiration", 3600);
        }});
    }

}
