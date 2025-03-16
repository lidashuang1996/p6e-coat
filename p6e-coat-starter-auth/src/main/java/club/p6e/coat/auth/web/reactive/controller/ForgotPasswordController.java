package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.aspect.Aspect;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.web.reactive.service.ForgotPasswordService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Handler
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class ForgotPasswordController {

    @PostMapping("/forgot/password")
    public Mono<ResultContext> def(ServerWebExchange exchange, @RequestBody ForgotPasswordContext.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> ServerHttpRequestParameterValidator.execute(exchange, request)) // verify request param
                .flatMap(o -> SpringUtil.getBean(ForgotPasswordService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
