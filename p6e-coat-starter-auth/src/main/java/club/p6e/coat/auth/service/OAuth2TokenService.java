package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.OAuth2Context;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * OAUTH2 TOKEN
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2TokenService {

    Mono<OAuth2Context.Token.Dto> execute(ServerWebExchange exchange, OAuth2Context.Token.Request param);

}
