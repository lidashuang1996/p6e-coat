package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Password Signature Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PasswordSignatureService {

    /**
     * Execute Password Signature
     *
     * @param exchange Server Web Exchange Object
     * @param param    Password Signature Context Request Object
     * @return Password Signature Context Dto Object
     */
    Mono<PasswordSignatureContext.Dto> execute(ServerWebExchange exchange, PasswordSignatureContext.Request param);

}
