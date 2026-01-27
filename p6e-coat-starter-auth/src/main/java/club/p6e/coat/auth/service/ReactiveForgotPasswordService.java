package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Forgot Password Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveForgotPasswordService {

    /**
     * Execute Forgot Password
     *
     * @param exchange Server Web Exchange Object
     * @param param    Forgot Password Context Request Object
     * @return Forgot Password Context Dto Object
     */
    Mono<ForgotPasswordContext.Dto> execute(ServerWebExchange exchange, ForgotPasswordContext.Request param);

}
