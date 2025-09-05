package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.ForgotPasswordVerificationCodeAcquisitionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Verification Code Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = ForgotPasswordVerificationCodeAcquisitionController.class,
        ignored = ForgotPasswordVerificationCodeAcquisitionController.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ForgotPasswordVerificationCodeAcquisitionController {

    /**
     * Forgot Password Verification Code Acquisition Service Object
     */
    private final ForgotPasswordVerificationCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Forgot Password Verification Code Acquisition Service Object
     */
    public ForgotPasswordVerificationCodeAcquisitionController(ForgotPasswordVerificationCodeAcquisitionService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Forgot Password Context Verification Code Acquisition Request Object
     * @return Forgot Password Context Verification Code Acquisition Request Object
     */
    private Mono<ForgotPasswordContext.VerificationCodeAcquisition.Request> validate(
            ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<ForgotPasswordContext.VerificationCodeAcquisition.Request> validate(ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping(value = "/forgot/password/code")
    public Mono<Object> def(ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
