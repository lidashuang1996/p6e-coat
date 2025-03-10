package club.p6e.coat.auth.handler;

import club.p6e.coat.auth.aspect.WebFluxAspect;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.service.QuickResponseCodeLoginService;
import club.p6e.coat.auth.service.QrCodeObtainService;
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
    @PostMapping(value = "/quick/response/code")
    public Mono<ResultContext> qrc(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCode.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeLoginService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

    @ResponseBody
    @GetMapping(value = "/quick/response/code")
    public Mono<ResultContext> qrc(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCodeObtain.Request request) {
        return WebFluxAspect
                .before(new Object[]{exchange, request})
                .flatMap(o -> SpringUtil.getBean(QrCodeObtainService.class).execute(exchange, request))
                .flatMap(m -> WebFluxAspect.after(new Object[]{exchange, request, m}))
                .map(ResultContext::build);
    }

}
