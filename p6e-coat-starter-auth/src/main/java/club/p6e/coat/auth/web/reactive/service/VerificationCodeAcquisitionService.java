package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeAcquisitionService {

    /**
     * Execute Verification Code Acquisition Operation
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Verification Code Acquisition Object
     * @return Login Context Verification Code Acquisition Dto Object
     */
    Mono<LoginContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request param);

}
