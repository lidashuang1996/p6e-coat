package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.service.ReactiveLoginAuthenticationService;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.common.exception.ServiceNotEnableException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Authentication Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveLoginAuthenticationController.class)
@RestController("club.p6e.coat.auth.controller.ReactiveLoginAuthenticationController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginAuthenticationController {

    /**
     * Reactive Login Authentication Service Object
     */
    private final ReactiveLoginAuthenticationService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Login Authentication Service Object
     */
    public ReactiveLoginAuthenticationController(ReactiveLoginAuthenticationService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Authentication Request Object
     * @return Login Context Authentication Request Object
     */
    private Mono<LoginContext.Authentication.Request> validate(
            ServerWebExchange exchange,
            LoginContext.Authentication.Request request
    ) {
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(new ParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.Authentication.Request> validate(ServerWebExchange exchange, LoginContext.Authentication.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/login/authentication")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.Authentication.Request request) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable()) {
            return validate(exchange, request).flatMap(r -> service.execute(exchange, r)).map(u -> new LoginContext.Authentication.Dto());
        } else {
            return Mono.error(new ServiceNotEnableException(
                    this.getClass(),
                    "fun Mono<Object> def(ServerWebExchange exchange, LoginContext.Authentication.Request request)",
                    "login authentication is not enabled"
            ));
        }
    }

}
