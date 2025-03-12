package club.p6e.coat.auth.web.reactive.handler;

import club.p6e.coat.auth.web.reactive.aspect.Aspect;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.web.reactive.service.ForgotPasswordService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Handler
 *
 * @author lidashuang
 * @version 1.0
 */
public class ForgotPasswordHandler {

    @ResponseBody
    @PostMapping()
    public Mono<ResultContext> fp(ServerWebExchange exchange, @RequestBody ForgotPasswordContext.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(ForgotPasswordService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
