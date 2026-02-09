package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.service.ReactiveLoginVerificationCodeAcquisitionService;
import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.common.exception.ServiceNotEnableException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Verification Code Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveLoginVerificationCodeAcquisitionController.class)
@RestController("club.p6e.coat.auth.controller.ReactiveLoginVerificationCodeAcquisitionController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginVerificationCodeAcquisitionController {

    /**
     * Reactive Login Verification Code Acquisition Service Object
     */
    private final ReactiveLoginVerificationCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Login Verification Code Acquisition Service Object
     */
    public ReactiveLoginVerificationCodeAcquisitionController(ReactiveLoginVerificationCodeAcquisitionService service) {
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
                .switchIfEmpty(Mono.error(new ParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.VerificationCodeAcquisition.Request> validate(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping(value = "/login/verification/code")
    public Mono<Object> def(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable() && properties.getLogin().getQuickResponseCode().isEnable()) {
            return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
        } else {
            return Mono.error(new ServiceNotEnableException(
                    this.getClass(),
                    "fun Mono<Object> def(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request request)",
                    "login verification code is not enabled"
            ));
        }
    }

}
