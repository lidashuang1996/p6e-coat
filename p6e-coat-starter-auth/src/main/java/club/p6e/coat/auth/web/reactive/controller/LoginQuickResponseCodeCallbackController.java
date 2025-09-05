package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.LoginQuickResponseCodeCallbackService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Quick Response Code Callback Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(LoginQuickResponseCodeCallbackController.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class LoginQuickResponseCodeCallbackController {

    /**
     * Login Quick Response Code Callback Service Object
     */
    private final LoginQuickResponseCodeCallbackService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Quick Response Code Callback Service Object
     */
    public LoginQuickResponseCodeCallbackController(LoginQuickResponseCodeCallbackService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Quick Response Code Callback Request Object
     * @return Login Context Quick Response Code Callback Request Object
     */
    private Mono<LoginContext.QuickResponseCodeCallback.Request> validate(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request request) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.QuickResponseCodeCallback.Request> validate(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping("/login/quick/response/info")
    public Mono<Object> def(ServerWebExchange exchange, LoginContext.QuickResponseCodeCallback.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
