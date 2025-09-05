package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Quick Response Code Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LoginQuickResponseCodeService {

    /**
     * Execute Login Quick Response Code
     *
     * @param param Login Context Quick Response Code Request Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request param);

}
