package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.service.ReactiveRegisterService;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.error.ServiceNotEnableException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Register Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveRegisterController.class)
@RestController("club.p6e.coat.auth.controller.ReactiveRegisterController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveRegisterController {

    /**
     * Reactive Register Service Object
     */
    private final ReactiveRegisterService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Register Service Object
     */
    public ReactiveRegisterController(ReactiveRegisterService service) {
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
                .switchIfEmpty(Mono.error(new ParameterException(
                        this.getClass(),
                        "fun Mono<RegisterContext.Request> validate(ServerWebExchange exchange, RegisterContext.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/register")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody RegisterContext.Request request) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getRegister().isEnable()) {
            return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
        } else {
            return Mono.error(new ServiceNotEnableException(
                    this.getClass(),
                    "fun Mono<Object> def(ServerWebExchange exchange, RegisterContext.Request request)",
                    "register is not enabled"
            ));
        }
    }

}
