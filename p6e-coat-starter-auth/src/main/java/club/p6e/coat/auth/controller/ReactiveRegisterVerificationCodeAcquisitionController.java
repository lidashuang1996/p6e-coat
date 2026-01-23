package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.RegisterVerificationCodeAcquisitionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Register Verification Code Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveRegisterVerificationCodeAcquisitionController.class,
        ignored = ReactiveRegisterVerificationCodeAcquisitionController.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
@RestController("club.p6e.coat.auth.web.reactive.controller.RegisterVerificationCodeAcquisitionController")
public class ReactiveRegisterVerificationCodeAcquisitionController {

    /**
     * Register Verification Code Acquisition Service Object
     */
    private final RegisterVerificationCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Register Verification Code Acquisition Service Object
     */
    public ReactiveRegisterVerificationCodeAcquisitionController(RegisterVerificationCodeAcquisitionService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Register Context Verification Code Acquisition Request Object
     * @return Register Context Verification Code Acquisition Request Object
     */
    private Mono<RegisterContext.VerificationCodeAcquisition.Request> validate(
            ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request request) {
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<RegisterContext.VerificationCodeAcquisition.Request> validate(ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping(value = "/register/code")
    public Mono<Object> def(ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
