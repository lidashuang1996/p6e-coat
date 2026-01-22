package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveTokenGenerator {

    /**
     * Execute Token Generate
     *
     * @param exchange Server Web Exchange Object
     * @param user     User Object
     * @return Result Object
     */
    Mono<Object> execute(ServerWebExchange exchange, User user);

}
