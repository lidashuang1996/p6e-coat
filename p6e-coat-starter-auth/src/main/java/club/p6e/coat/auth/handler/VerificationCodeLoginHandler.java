package club.p6e.coat.auth.handler;

import club.p6e.coat.auth.aspect.WebFluxAspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.service.*;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeLoginHandler {

    @ResponseBody
    @PostMapping(value = "/verification/code")
    public Mono<ResultContext> vc(ServerWebExchange exchange, @RequestBody LoginContext.VerificationCode.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(VerificationCodeLoginService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

    @ResponseBody
    @GetMapping(value = "/verification/code")
    public Mono<ResultContext> vc(ServerWebExchange exchange, @RequestBody LoginContext.VerificationCodeObtain.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(VerificationCodeAcquisitionService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
