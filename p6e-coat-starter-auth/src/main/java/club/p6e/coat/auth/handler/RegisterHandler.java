package club.p6e.coat.auth.handler;

import club.p6e.coat.auth.aspect.WebFluxAspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.service.QrCodeLoginService;
import club.p6e.coat.auth.service.RegisterService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class RegisterHandler {

    @ResponseBody
    @PostMapping(value = "/register")
    public Mono<ResultContext> register(ServerWebExchange exchange, @RequestBody RegisterContext.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(RegisterService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
