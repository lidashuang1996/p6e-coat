package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.RegisterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Register Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveRegisterController.class,
        ignored = ReactiveRegisterController.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
@RestController("club.p6e.coat.auth.web.reactive.controller.RegisterController")
public class ReactiveRegisterController {

    /**
     * Register Service Object
     */
    private final RegisterService service;

    /**
     * Constructor Initialization
     *
     * @param service Register Service Object
     */
    public ReactiveRegisterController(RegisterService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Password Signature Context Request Object
     * @return Password Signature Context Request Object
     */
    private Mono<RegisterContext.Request> validate(ServerWebExchange exchange, RegisterContext.Request request) {
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<RegisterContext.Request> validate(ServerWebExchange exchange, RegisterContext.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/register")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody RegisterContext.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
