package club.p6e.coat.auth.service;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Logout Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveLogoutService {

    /**
     * Execute
     *
     * @param exchange Server Web Exchange Object
     * @return Result Object
     */
    Mono<Object> execute(ServerWebExchange exchange);

}
