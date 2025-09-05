package club.p6e.coat.auth.web.reactive.service;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Index Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface IndexService {

    /**
     * Execute Index
     *
     * @param exchange Server Web Exchange Object
     * @return String Object
     */
    Mono<String[]> execute(ServerWebExchange exchange);

}
