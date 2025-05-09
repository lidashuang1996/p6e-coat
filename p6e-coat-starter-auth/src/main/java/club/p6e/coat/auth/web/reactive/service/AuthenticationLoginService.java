package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AuthenticationLoginService {

    /**
     * Execute Authorization Verification
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Authentication Request Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param);

}
