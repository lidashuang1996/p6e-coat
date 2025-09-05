package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.LoginAccountPasswordService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Account Password Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(LoginAccountPasswordController.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class LoginAccountPasswordController {

    /**
     * Login Account Password Service Object
     */
    private final LoginAccountPasswordService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Account Password Service Object
     */
    public LoginAccountPasswordController(LoginAccountPasswordService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Account Password Request Object
     * @return Login Context Account Password Request Object
     */
    private Mono<LoginContext.AccountPassword.Request> validate(
            ServerWebExchange exchange,
            LoginContext.AccountPassword.Request request
    ) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.AccountPassword.Request> validate(ServerWebExchange exchange, LoginContext.AccountPassword.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/login/account/password")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.AccountPassword.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
