package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Callback Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QuickResponseCodeLoginService {

    /**
     * Execute Quick Response Code Callback
     *
     * @param param Quick Response Code Callback Request Object
     * @return Login Context Quick Response Code Callback Dto Object
     */
    Mono<LoginContext.QuickResponseCodeCallback.Dto> execute(
            ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param);

}
