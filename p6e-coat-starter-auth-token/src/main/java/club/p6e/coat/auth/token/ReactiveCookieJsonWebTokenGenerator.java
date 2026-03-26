package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive Cookie Json Web Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveCookieJsonWebTokenGenerator implements ReactiveTokenGenerator {

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public ReactiveCookieJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final int duration = duration();
        final String device = device(exchange);
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        final String content = this.codec.encryption(user.id(), (device == null ? "PC" : device) + "@" + user.serialize(), duration);
        return Mono
                .just(content)
                .flatMap(c -> Mono.just(cookie(name(), c)))
                .flatMap(c -> {
                    response.addCookie(c);
                    return Mono.just(LocalDateTime.now());
                });
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
     * Get Cookie Name
     *
     * @return Cookie Name
     */
    public String name() {
        return "P6E_AUTH";
    }

    /**
     * Get Cookie Duration
     *
     * @return Cookie Duration
     */
    public int duration() {
        return 3600;
    }

    /**
     * Set Cookie
     *
     * @param name    Cookie Name
     * @param content Cookie Content
     * @return Response Cookie Object
     */
    public ResponseCookie cookie(String name, String content) {
        return ResponseCookie.from(name, content).path("/").maxAge(duration()).httpOnly(true).build();
    }

}
