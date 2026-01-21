package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveTokenValidator {

    /**
     * Execute Token Validate
     *
     * @param exchange Server Web Exchange Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange);

}
