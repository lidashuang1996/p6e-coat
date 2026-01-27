package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.RegisterContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Register Verification Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveVerificationCodeAcquisitionService {

    /**
     * Execute Register Verification Code Acquisition
     *
     * @param exchange Server Web Exchange Object
     * @param param    Register Context Verification Code Acquisition Request Object
     * @return Register Context Verification Code Acquisition Request Object
     */
    Mono<RegisterContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request param);

}
