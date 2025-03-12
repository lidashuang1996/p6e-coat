package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Forgot Password Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeForgotPasswordAcquisitionService {

    /**
     * 忘记密码发送验证码
     *
     * @param exchange Server Web Exchange Object
     * @param param    Forgot Password Context Verification Code Acquisition Request Object
     * @return Forgot Password Context Verification Code Acquisition Dto Object
     */
    Mono<ForgotPasswordContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request param);

}
