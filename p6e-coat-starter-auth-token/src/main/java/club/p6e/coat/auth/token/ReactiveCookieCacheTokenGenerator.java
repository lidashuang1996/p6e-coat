package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive Cookie Cache Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveCookieCacheTokenGenerator implements ReactiveTokenGenerator {

    /**
     * User Token Cache Object
     */
    protected final ReactiveUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Reactive User Token Cache Object
     */
    public ReactiveCookieCacheTokenGenerator(ReactiveUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final String token = token();
        final String device = device(exchange);
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        return this.cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), duration())
                .flatMap(m -> Mono.just(cookie(name(), token)))
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
     * Get Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + System.currentTimeMillis() + GeneratorUtil.random(8, true, false);
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
