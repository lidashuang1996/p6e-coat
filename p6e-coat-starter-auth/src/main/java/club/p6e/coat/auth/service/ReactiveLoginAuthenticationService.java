package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Authentication Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveLoginAuthenticationService {

    /**
     * Execute Login Authentication
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Authentication Request Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param);

}
