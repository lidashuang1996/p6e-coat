package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.service.ReactiveLoginQuickResponseCodeService;
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
 * Reactive Login Quick Response Code Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController("club.p6e.coat.auth.controller.ReactiveLoginQuickResponseCodeController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginQuickResponseCodeController {

    /**
     * Reactive Login Quick Response Code Service Object
     */
    private final ReactiveLoginQuickResponseCodeService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Login Quick Response Code Service Object
     */
    public ReactiveLoginQuickResponseCodeController(ReactiveLoginQuickResponseCodeService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Quick Response Code Request Object
     * @return Login Context Quick Response Code Request Object
     */
    private Mono<LoginContext.QuickResponseCode.Request> validate(
            ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request) {
        return ReactiveRequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(new ParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.QuickResponseCode.Request> validate(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request)",
                        "request parameter validation exception"
                )));
    }

    @PostMapping("/login/quick/response/code")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCode.Request request) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable() && properties.getLogin().getQuickResponseCode().isEnable()) {
            return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
        } else {
            return Mono.error(new ServiceNotEnableException(
                    this.getClass(),
                    "fun Mono<Object> def(ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request)",
                    "login quick response code is not enabled"
            ));
        }
    }

}
