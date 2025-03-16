package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.SimpleUserModel;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.AccountPasswordLoginService;
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
public class AccountPasswordLoginController {

    @PostMapping("/login/account/password")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.AccountPassword.Request request) {
        final AccountPasswordLoginService service = SpringUtil.getBean(AccountPasswordLoginService.class);
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> service.execute(exchange, ((o instanceof LoginContext.AccountPassword.Request) ? (LoginContext.AccountPassword.Request) o : null)));
    }

}
