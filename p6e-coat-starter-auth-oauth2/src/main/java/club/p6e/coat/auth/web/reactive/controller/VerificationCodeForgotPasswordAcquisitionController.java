package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeForgotPasswordAcquisitionService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Forgot Password Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = VerificationCodeForgotPasswordAcquisitionController.class,
        ignored = VerificationCodeForgotPasswordAcquisitionController.class
)
public class VerificationCodeForgotPasswordAcquisitionController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Forgot Password Context Verification Code Acquisition Request Object
     * @return Forgot Password Context Verification Code Acquisition Request Object
     */
    private Mono<ForgotPasswordContext.VerificationCodeAcquisition.Request> validate(
            ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof ForgotPasswordContext.VerificationCodeAcquisition.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<ForgotPasswordContext.VerificationCodeAcquisition.Request> validate(" +
                                        "ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @GetMapping(value = "/forgot/password/code")
    public Mono<Object> def(ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(VerificationCodeForgotPasswordAcquisitionService.class).execute(exchange, r));
    }

}
