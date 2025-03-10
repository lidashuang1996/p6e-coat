package club.p6e.coat.auth.handler;

import club.p6e.coat.auth.aspect.WebFluxAspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.service.AccountPasswordLoginService;
import club.p6e.coat.auth.service.AccountPasswordLoginSignatureService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Account Password Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
public class AccountPasswordLoginHandler {

    @ResponseBody
    @PostMapping(value = "/account/password")
    public Mono<ResultContext> ap(ServerWebExchange exchange, @RequestBody LoginContext.AccountPassword.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(AccountPasswordLoginService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

    @ResponseBody
    @PostMapping(value = "/account/password/signature")
    public Mono<ResultContext> aps(ServerWebExchange exchange, @RequestBody LoginContext.AccountPasswordSignature.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(AccountPasswordLoginSignatureService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
