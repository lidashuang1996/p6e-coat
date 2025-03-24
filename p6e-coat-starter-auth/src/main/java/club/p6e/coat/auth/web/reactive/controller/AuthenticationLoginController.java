package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.AuthenticationLoginService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = AuthenticationLoginController.class,
        ignored = AuthenticationLoginController.class
)
public class AuthenticationLoginController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Authentication Request Object
     * @return Login Context Authentication Request Object
     */
    private Mono<LoginContext.Authentication.Request> validate(
            ServerWebExchange exchange, LoginContext.Authentication.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof LoginContext.Authentication.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<LoginContext.Authentication.Request> validate(" +
                                        "ServerWebExchange exchange, LoginContext.Authentication.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @PostMapping("/login/authentication")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.Authentication.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(
                AuthenticationLoginService.class).execute(exchange, r)).map(o -> "AUTHENTICATION");
    }

}
