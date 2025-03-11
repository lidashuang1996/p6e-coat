package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface TokenValidator extends club.p6e.coat.auth.TokenValidator<ServerWebExchange, Mono<User>> {
}
