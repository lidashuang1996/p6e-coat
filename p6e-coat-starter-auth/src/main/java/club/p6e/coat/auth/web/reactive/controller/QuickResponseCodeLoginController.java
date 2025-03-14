package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.aspect.Aspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginService;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeAcquisitionService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class QuickResponseCodeLoginController {

    @PostMapping("/login/quick/response/code")
    public Mono<ResultContext> qrc(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCode.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> ServerHttpRequestParameterValidator.execute(exchange, request)) // verify request param
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeLoginService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

    @GetMapping("/login/quick/response/code")
    public Mono<ResultContext> qrc(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCodeAcquisition.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> ServerHttpRequestParameterValidator.execute(exchange, request)) // verify request param
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeAcquisitionService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
