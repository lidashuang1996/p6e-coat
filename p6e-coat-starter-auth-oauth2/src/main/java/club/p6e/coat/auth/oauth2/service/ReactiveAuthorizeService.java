package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Authorize Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveAuthorizeService {

    /**
     * Execute
     *
     * @param exchange Server Web Exchange Object
     * @param request  Authorize Context Request Object
     * @return Index Context Dto Object
     */
    Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request);

}
