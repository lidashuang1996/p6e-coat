package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthUser;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface QqOtherLoginService {

    Mono<String> home(ServerWebExchange exchange);

    Mono<AuthUser.Model> callback(ServerWebExchange exchange);

}
