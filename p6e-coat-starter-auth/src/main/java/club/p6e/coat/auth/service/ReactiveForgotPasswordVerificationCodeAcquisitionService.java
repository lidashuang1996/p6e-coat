package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Forgot Password Verification Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveForgotPasswordVerificationCodeAcquisitionService {

    /**
     * Execute Forgot Password Verification Code Acquisition
     *
     * @param exchange Server Web Exchange Object
     * @param param    Forgot Password Context Verification Code Acquisition Request Object
     * @return Forgot Password Context Verification Code Acquisition Dto Object
     */
    Mono<ForgotPasswordContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request param);

}
