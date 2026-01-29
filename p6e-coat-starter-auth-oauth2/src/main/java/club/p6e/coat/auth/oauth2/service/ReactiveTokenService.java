package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.context.TokenContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Reactive Token Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveTokenService {

    /**
     * Execute
     *
     * @param exchange Server Web Exchange Object
     * @param request  Token Context Request Object
     * @return Result Object
     */
    Mono<Map<String, Object>> execute(ServerWebExchange exchange, TokenContext.Request request);

}
