package club.p6e.coat.auth.web.reactive.handler;

import club.p6e.coat.auth.web.reactive.aspect.Aspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginService;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeAcquisitionService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
public class QuickResponseCodeLoginHandler {

    @ResponseBody
    @PostMapping("/quick/response/code")
    public Mono<ResultContext> qrc(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCode.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeLoginService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

    @ResponseBody
    @GetMapping("/quick/response/code")
    public Mono<ResultContext> qrc(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCodeObtain.Request request) {
        return Aspect
                .executeBefore(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeAcquisitionService.class).execute(exchange, request))
                .flatMap(m -> Aspect.executeAfter(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
