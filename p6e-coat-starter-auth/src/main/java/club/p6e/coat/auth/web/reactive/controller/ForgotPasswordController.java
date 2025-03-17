package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.web.reactive.service.ForgotPasswordService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class ForgotPasswordController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Forgot Password Context Request Object
     * @return Forgot Password Context Request Object
     */
    private Mono<ForgotPasswordContext.Request> validate(
            ServerWebExchange exchange, ForgotPasswordContext.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof ForgotPasswordContext.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<ForgotPasswordContext.Request> validate(" +
                                        "ServerWebExchange exchange, ForgotPasswordContext.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @PostMapping("/forgot/password")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody ForgotPasswordContext.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(ForgotPasswordService.class).execute(exchange, r));
    }

}
