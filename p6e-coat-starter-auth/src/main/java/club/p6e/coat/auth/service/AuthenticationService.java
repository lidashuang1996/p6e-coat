package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface AuthenticationService {
    Mono<AuthUser.Model> execute(ServerWebExchange exchange, LoginContext.Authentication.Request param);
}
