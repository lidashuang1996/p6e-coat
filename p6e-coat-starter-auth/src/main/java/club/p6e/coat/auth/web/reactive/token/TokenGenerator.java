package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TokenGenerator {
    Mono<Object> execute(ServerWebExchange exchange, User user);
}
