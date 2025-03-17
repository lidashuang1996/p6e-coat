package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.PasswordSignatureService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Account Password Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class PasswordSignatureController {

    @GetMapping("/password/signature")
    public Mono<Object> def(ServerWebExchange exchange, PasswordSignatureContext.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> SpringUtil.getBean(PasswordSignatureService.class).execute(exchange, request));
    }

}
