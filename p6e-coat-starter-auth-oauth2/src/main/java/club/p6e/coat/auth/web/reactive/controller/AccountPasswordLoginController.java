package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.AccountPasswordLoginService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Account Password Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = AccountPasswordLoginController.class,
        ignored = AccountPasswordLoginController.class
)
public class AccountPasswordLoginController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Account Password Request Object
     * @return Login Context Account Password Request Object
     */
    private Mono<LoginContext.AccountPassword.Request> validate(
            ServerWebExchange exchange, LoginContext.AccountPassword.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof LoginContext.AccountPassword.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<LoginContext.AccountPassword.Request> validate(" +
                                        "ServerWebExchange exchange, LoginContext.AccountPassword.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @PostMapping("/login/account/password")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.AccountPassword.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(AccountPasswordLoginService.class).execute(exchange, r));
    }

}
