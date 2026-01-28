package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Quick Response Code Callback Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveLoginQuickResponseCodeCallbackService {

    /**
     * Execute Login Quick Response Code Callback
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Quick Response Code Callback Request Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param);

}
