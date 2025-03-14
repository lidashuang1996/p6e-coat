package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.aspect.Aspect;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.web.reactive.service.RegisterService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Register Handler
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class RegisterHandler {

    @PostMapping("/register")
    public Mono<ResultContext> def(ServerWebExchange exchange, @RequestBody RegisterContext.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> ServerHttpRequestParameterValidator.execute(exchange, request)) // verify request param
                .flatMap(o -> SpringUtil.getBean(RegisterService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
