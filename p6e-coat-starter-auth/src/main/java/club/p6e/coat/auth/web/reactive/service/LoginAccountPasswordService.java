package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Account Password Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LoginAccountPasswordService {

    /**
     * Execute Login Account Password
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Account Password Request Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.AccountPassword.Request param);

}
