package club.p6e.coat.auth.token;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveTokenCleaner {

    /**
     * Execute Token Cleaner
     *
     * @param exchange Server Web Exchange Object
     * @return Result Object
     */
    Mono<Object> execute(ServerWebExchange exchange);

}
