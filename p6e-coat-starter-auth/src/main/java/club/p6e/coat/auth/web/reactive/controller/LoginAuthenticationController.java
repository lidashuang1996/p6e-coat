package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.LoginAuthenticationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Authentication Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = LoginAuthenticationController.class,
        ignored = LoginAuthenticationController.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class LoginAuthenticationController {

    /**
     * Login Authentication Service Object
     */
    private final LoginAuthenticationService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Authentication Service Object
     */
    public LoginAuthenticationController(LoginAuthenticationService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Authentication Request Object
     * @return Login Context Authentication Request Object
     */
    private Mono<LoginContext.Authentication.Request> validate(
            ServerWebExchange exchange,
            LoginContext.Authentication.Request request
    ) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.Authentication.Request> validate(ServerWebExchange exchange, LoginContext.Authentication.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/login/authentication")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.Authentication.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r)).map(u -> "AUTHENTICATION");
    }

}
