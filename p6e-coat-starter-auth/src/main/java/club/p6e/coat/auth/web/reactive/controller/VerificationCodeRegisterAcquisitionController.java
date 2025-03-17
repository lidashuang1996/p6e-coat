package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeRegisterAcquisitionService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Register Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class VerificationCodeRegisterAcquisitionController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Register Context Verification Code Acquisition Request Object
     * @return Register Context Verification Code Acquisition Request Object
     */
    private Mono<RegisterContext.VerificationCodeAcquisition.Request> validate(
            ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof RegisterContext.VerificationCodeAcquisition.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<RegisterContext.VerificationCodeAcquisition.Request> validate(" +
                                        "ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @GetMapping(value = "/register/code")
    public Mono<Object> def(ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(VerificationCodeRegisterAcquisitionService.class).execute(exchange, r));
    }

}
