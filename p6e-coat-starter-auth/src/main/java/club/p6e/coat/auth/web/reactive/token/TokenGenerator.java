package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.user.User;
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
