package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.IndexContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Index Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveIndexService {

    /**
     * Execute Index
     *
     * @param exchange Server Web Exchange Object
     * @return Index Context Dto Object
     */
    Mono<IndexContext.Dto> execute(ServerWebExchange exchange);

}
