package club.p6e.coat.auth.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.User;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxLocalStorageJsonWebTokenGenerator implements WebFluxTokenGenerator {

    private final JsonWebTokenCodec codec;

    public WebFluxLocalStorageJsonWebTokenGenerator(JsonWebTokenCodec codec) {
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
