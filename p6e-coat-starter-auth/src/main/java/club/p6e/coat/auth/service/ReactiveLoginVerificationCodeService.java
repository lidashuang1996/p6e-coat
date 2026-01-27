package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Verification Code Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveLoginVerificationCodeService {

    /**
     * Execute Login Verification Code
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Verification Code Request Object
     * @return User Object
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param);

}
