package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeLoginService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = VerificationCodeLoginController.class,
        ignored = VerificationCodeLoginController.class
)
public class VerificationCodeLoginController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Verification Code Request Object
     * @return Login Context Verification Code Request Object
     */
    private Mono<LoginContext.VerificationCode.Request> validate(
            ServerWebExchange exchange, LoginContext.VerificationCode.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof LoginContext.VerificationCode.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<LoginContext.VerificationCode.Request> validate(" +
                                        "ServerWebExchange exchange, LoginContext.VerificationCode.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @PostMapping(value = "/login/verification/code")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.VerificationCode.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(VerificationCodeLoginService.class).execute(exchange, r));
    }

}
