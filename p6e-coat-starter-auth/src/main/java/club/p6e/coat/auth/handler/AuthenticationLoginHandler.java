package club.p6e.coat.auth.handler;

import club.p6e.coat.auth.aspect.WebFluxAspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.service.AuthenticationService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthenticationLoginHandler {

    @ResponseBody
    @PostMapping(value = "/authentication")
    public Mono<ResultContext> authentication(ServerWebExchange exchange, @RequestBody LoginContext.Authentication.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(AuthenticationService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
