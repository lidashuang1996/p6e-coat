package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QuickResponseCodeLoginCallbackService {

    /**
     * Execute Quick Response Code Login
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Quick Response Code Request Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request param);

}
