package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.service.ReactivePasswordSignatureService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Password Signature Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactivePasswordSignatureController.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
@RestController("club.p6e.coat.auth.controller.PasswordSignatureController")
public class ReactivePasswordSignatureController {

    /**
     * Password Signature Service Object
     */
    private final ReactivePasswordSignatureService service;

    /**
     * Constructor Initialization
     *
     * @param service Password Signature Service Object
     */
    public ReactivePasswordSignatureController(ReactivePasswordSignatureService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Password Signature Context Request Object
     * @return Password Signature Context Request Object
     */
    private Mono<PasswordSignatureContext.Request> validate(
            ServerWebExchange exchange, PasswordSignatureContext.Request request) {
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<PasswordSignatureContext.Request> validate(ServerWebExchange exchange, PasswordSignatureContext.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping("/password/signature")
    public Mono<Object> def(ServerWebExchange exchange, PasswordSignatureContext.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
