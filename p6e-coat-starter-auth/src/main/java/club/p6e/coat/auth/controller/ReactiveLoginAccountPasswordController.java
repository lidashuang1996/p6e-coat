package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.service.ReactiveLoginAccountPasswordService;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Account Password Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveLoginAccountPasswordController.class)
@RestController("club.p6e.coat.auth.controller.ReactiveLoginAccountPasswordService")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginAccountPasswordController {

    /**
     * Reactive Login Account Password Service Object
     */
    private final ReactiveLoginAccountPasswordService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Login Account Password Service Object
     */
    public ReactiveLoginAccountPasswordController(ReactiveLoginAccountPasswordService service) {
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
        return ReactiveRequestParameterValidator.run(exchange, request)
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
