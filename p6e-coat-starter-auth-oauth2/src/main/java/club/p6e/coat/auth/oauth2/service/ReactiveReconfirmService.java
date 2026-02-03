package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Reactive Reconfirm Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveReconfirmService {

    /**
     * Execute
     *
     * @param exchange Server Web Exchange Object
     * @param request  Reconfirm Context Request Object
     * @return Result Object
     */
    Mono<Map<String, String>> execute(ServerWebExchange exchange, ReconfirmContext.Request request);

}
