package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.user.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TokenValidator {

    /**
     * Execute Token Validate
     *
     * @param exchange Server Web Exchange Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange);

}
