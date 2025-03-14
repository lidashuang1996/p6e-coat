package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.aspect.Aspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeAcquisitionService;
import club.p6e.coat.auth.web.reactive.service.VerificationCodeLoginService;
import club.p6e.coat.common.context.ResultContext;
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
public class VerificationCodeLoginHandler {

    @PostMapping(value = "/verification/code")
    public Mono<ResultContext> def(ServerWebExchange exchange, @RequestBody LoginContext.VerificationCode.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> ServerHttpRequestParameterValidator.execute(exchange, request)) // verify request param
                .flatMap(o -> SpringUtil.getBean(VerificationCodeLoginService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

    @GetMapping(value = "/verification/code")
    public Mono<ResultContext> def(ServerWebExchange exchange, @RequestBody LoginContext.VerificationCodeAcquisition.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> ServerHttpRequestParameterValidator.execute(exchange, request)) // verify request param
                .flatMap(o -> SpringUtil.getBean(VerificationCodeAcquisitionService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
