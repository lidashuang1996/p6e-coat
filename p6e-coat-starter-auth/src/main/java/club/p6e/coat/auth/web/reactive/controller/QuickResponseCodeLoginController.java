package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginCallbackService;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginCallbackServiceImpl;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginService;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginAcquisitionService;
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

    @GetMapping("/login/quick/response/code")
    public Mono<Object> code(ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeLoginAcquisitionService.class).execute(exchange, request));
    }

    @PostMapping("/login/quick/response/code")
    public Mono<Object> login(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCodeCallback.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeLoginCallbackService.class).execute(exchange, request));
    }

    @GetMapping("/login/quick/response/code/info")
    public Mono<Object> info(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> SpringUtil.getBean(QuickResponseCodeLoginService.class).execute(exchange, request));
    }

}
