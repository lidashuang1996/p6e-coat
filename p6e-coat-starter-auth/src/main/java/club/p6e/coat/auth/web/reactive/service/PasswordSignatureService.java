package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Account Password Login Signature Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PasswordSignatureService {

    /**
     * Execute Account Password Login Password Signature
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Account Password Signature.Request Object
     * @return Login Context Account Password Signature Dto Object
     */
    Mono<LoginContext.AccountPasswordSignature.Dto> execute(
            ServerWebExchange exchange, LoginContext.AccountPasswordSignature.Request param);

}
