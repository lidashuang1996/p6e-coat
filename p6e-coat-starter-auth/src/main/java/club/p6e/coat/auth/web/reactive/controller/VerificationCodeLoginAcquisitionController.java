package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeLoginAcquisitionService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Login Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = VerificationCodeLoginAcquisitionController.class,
        ignored = VerificationCodeLoginAcquisitionController.class
)
public class VerificationCodeLoginAcquisitionController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Verification Code Acquisition Request Object
     * @return Login Context Verification Code Acquisition Request Object
     */
    private Mono<LoginContext.VerificationCodeAcquisition.Request> validate(
            ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof LoginContext.VerificationCodeAcquisition.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<LoginContext.VerificationCodeAcquisition.Request> validate(" +
                                        "ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @GetMapping(value = "/login/verification/code")
    public Mono<Object> def(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(VerificationCodeLoginAcquisitionService.class).execute(exchange, r));
    }

}
