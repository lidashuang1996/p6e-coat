package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeLoginAcquisitionService;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeLoginService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class VerificationCodeLoginController {

    @GetMapping(value = "/login/verification/code")
    public Mono<Object> def(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request) {
        final VerificationCodeLoginAcquisitionService service = SpringUtil.getBean(VerificationCodeLoginAcquisitionService.class);
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> service.execute(exchange, request));
    }

    @PostMapping(value = "/login/verification/code")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.VerificationCode.Request request) {
        final VerificationCodeLoginService service = SpringUtil.getBean(VerificationCodeLoginService.class);
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> service.execute(exchange, request));
    }

}
