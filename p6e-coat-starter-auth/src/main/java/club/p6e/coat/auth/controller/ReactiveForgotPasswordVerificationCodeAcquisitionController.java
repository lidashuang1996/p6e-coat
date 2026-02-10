package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.service.ReactiveForgotPasswordVerificationCodeAcquisitionService;
import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.common.exception.ServiceNotEnableException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Forgot Password Verification Code Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController("club.p6e.coat.auth.controller.ReactiveForgotPasswordVerificationCodeAcquisitionController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveForgotPasswordVerificationCodeAcquisitionController {

    /**
     * Reactive Forgot Password Verification Code Acquisition Service Object
     */
    private final ReactiveForgotPasswordVerificationCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Forgot Password Verification Code Acquisition Service Object
     */
    public ReactiveForgotPasswordVerificationCodeAcquisitionController(ReactiveForgotPasswordVerificationCodeAcquisitionService service) {
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
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(new ParameterException(
                        this.getClass(),
                        "fun Mono<ForgotPasswordContext.VerificationCodeAcquisition.Request> validate(ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping(value = "/forgot/password/code")
    public Mono<Object> def(ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getForgotPassword().isEnable()) {
            return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
        } else {
            return Mono.error(new ServiceNotEnableException(
                    this.getClass(),
                    "fun Mono<Object> def(ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request request)",
                    "forgot password is not enabled"
            ));
        }
    }

}
