package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.LoginVerificationCodeAcquisitionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Verification Code Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveLoginVerificationCodeAcquisitionController.class,
        ignored = ReactiveLoginVerificationCodeAcquisitionController.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
@RestController("club.p6e.coat.auth.web.reactive.controller.LoginVerificationCodeAcquisitionController")
public class ReactiveLoginVerificationCodeAcquisitionController {

    /**
     * Login Verification Code Acquisition Service Object
     */
    private final LoginVerificationCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Verification Code Acquisition Service Object
     */
    public ReactiveLoginVerificationCodeAcquisitionController(LoginVerificationCodeAcquisitionService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Verification Code Acquisition Request Object
     * @return Login Context Verification Code Acquisition Request Object
     */
    private Mono<LoginContext.VerificationCodeAcquisition.Request> validate(
            ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request) {
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.VerificationCodeAcquisition.Request> validate(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping(value = "/login/verification/code")
    public Mono<Object> def(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
