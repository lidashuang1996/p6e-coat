package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.LoginQuickResponseCodeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Quick Response Code Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(LoginQuickResponseCodeController.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class LoginQuickResponseCodeController {

    /**
     * Login Quick Response Code Service Object
     */
    private final LoginQuickResponseCodeService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Quick Response Code Service Object
     */
    public LoginQuickResponseCodeController(LoginQuickResponseCodeService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Quick Response Code Request Object
     * @return Login Context Quick Response Code Request Object
     */
    private Mono<LoginContext.QuickResponseCode.Request> validate(
            ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.QuickResponseCode.Request> validate(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/login/quick/response/code")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCode.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
