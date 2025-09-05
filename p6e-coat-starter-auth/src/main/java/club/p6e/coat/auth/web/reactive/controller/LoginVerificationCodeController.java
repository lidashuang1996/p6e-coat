package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.LoginVerificationCodeService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Verification Code Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = LoginVerificationCodeController.class,
        ignored = LoginVerificationCodeController.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class LoginVerificationCodeController {

    /**
     * Login Verification Code Service Object
     */
    private final LoginVerificationCodeService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Verification Code Service Object
     */
    public LoginVerificationCodeController(LoginVerificationCodeService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Verification Code Request Object
     * @return Login Context Verification Code Request Object
     */
    private Mono<LoginContext.VerificationCode.Request> validate(
            ServerWebExchange exchange, LoginContext.VerificationCode.Request request) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.VerificationCode.Request> validate(ServerWebExchange exchange, LoginContext.VerificationCode.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping(value = "/login/verification/code")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.VerificationCode.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
