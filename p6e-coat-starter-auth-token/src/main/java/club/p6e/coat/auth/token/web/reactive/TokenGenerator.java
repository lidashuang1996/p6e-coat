package club.p6e.coat.auth.token.web.reactive;

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

    /**
     * Execute Token Generate
     *
     * @param exchange Server Web Exchange Object
     * @param user     User Object
     * @return Result Object
     */
    Mono<Object> execute(ServerWebExchange exchange, User user);

}
