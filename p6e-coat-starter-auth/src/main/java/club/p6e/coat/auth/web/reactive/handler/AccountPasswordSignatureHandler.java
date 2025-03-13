package club.p6e.coat.auth.web.reactive.handler;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.aspect.Aspect;
import club.p6e.coat.auth.web.reactive.service.AccountPasswordLoginService;
import club.p6e.coat.auth.web.reactive.service.PasswordSignatureService;
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
public class AccountPasswordSignatureHandler {

    @ResponseBody
    @PostMapping("/account/password")
    public Mono<ResultContext> ap(ServerWebExchange exchange, @RequestBody LoginContext.AccountPassword.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(AccountPasswordLoginService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

    @ResponseBody
    @PostMapping("/account/password/signature")
    public Mono<ResultContext> aps(ServerWebExchange exchange, @RequestBody LoginContext.AccountPasswordSignature.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(PasswordSignatureService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
