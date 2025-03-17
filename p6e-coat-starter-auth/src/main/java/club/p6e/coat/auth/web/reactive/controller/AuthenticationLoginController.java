package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.AuthenticationLoginService;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Login Handler
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
public class AuthenticationLoginController {

    @PostMapping("/login/authentication")
    public Mono<Object> authentication(ServerWebExchange exchange, @RequestBody LoginContext.Authentication.Request request) {
        final AuthenticationLoginService service = SpringUtil.getBean(AuthenticationLoginService.class);
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> service.execute(exchange, ((o instanceof LoginContext.Authentication.Request) ? (LoginContext.Authentication.Request) o : null)))
                .map(o -> "AUTHENTICATION");
    }

}
