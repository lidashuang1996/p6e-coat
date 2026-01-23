package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.ForgotPasswordService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveForgotPasswordController.class)
@RestController("club.p6e.coat.auth.web.reactive.controller.ForgotPasswordController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveForgotPasswordController {

    /**
     * Forgot Password Service Object
     */
    private final ForgotPasswordService service;

    /**
     * Constructor Initialization
     *
     * @param service Forgot Password Service Object
     */
    public ReactiveForgotPasswordController(ForgotPasswordService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Forgot Password Context Request Object
     * @return Forgot Password Context Request Object
     */
    private Mono<ForgotPasswordContext.Request> validate(ServerWebExchange exchange, ForgotPasswordContext.Request request) {
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<ForgotPasswordContext.Request> validate(ServerWebExchange exchange, ForgotPasswordContext.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/forgot/password")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody ForgotPasswordContext.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
