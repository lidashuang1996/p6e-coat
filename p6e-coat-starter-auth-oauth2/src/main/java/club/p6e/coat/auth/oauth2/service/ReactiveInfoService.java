package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.context.InfoContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Reactive Info Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveInfoService {

    /**
     * Get User Info
     *
     * @param exchange Server Web Exchange Object
     * @param request  Authorize Context Request Object
     * @return Result Object
     */
    Mono<Map<String, Object>> getUserInfo(ServerWebExchange exchange, InfoContext.Request request);

    /**
     * Get Client Info
     *
     * @param exchange Server Web Exchange Object
     * @param request  Authorize Context Request Object
     * @return Result Object
     */
    Mono<Map<String, Object>> getClientInfo(ServerWebExchange exchange, InfoContext.Request request);

}
