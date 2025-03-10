package club.p6e.coat.auth.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.User;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxCookieJsonWebTokenGenerator implements WebFluxTokenGenerator {

    private final JsonWebTokenCodec codec;

    public WebFluxCookieJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange context, User user) {
        final String device = context.getAttribute("P6e-Device");
        final String content = codec.encryption(user.id(), device, user.serialize());
        final ServerHttpResponse response = context.getResponse();
        return Mono.just(content).flatMap(c -> {
            response.addCookie(ResponseCookie.from("P6e-User-Auth", c).maxAge(3600).secure(true).build());
            return Mono.just("SUCCESS");
        });
    }

}
