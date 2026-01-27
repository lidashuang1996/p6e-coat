package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.RegisterContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Register Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveRegisterService {

    /**
     * Execution Register
     *
     * @param exchange Server Web Exchange Object
     * @param param    Register Context Request Object
     * @return Register Context Dto Object
     */
    Mono<RegisterContext.Dto> execute(ServerWebExchange exchange, RegisterContext.Request param);

}
