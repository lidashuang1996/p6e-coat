package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.service.ReactiveLogoutService;
import club.p6e.coat.common.exception.ServiceNotEnableException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Register Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController("club.p6e.coat.auth.controller.ReactiveLogoutController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLogoutController {

    /**
     * Reactive Logout Service Object
     */
    private final ReactiveLogoutService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Logout Service Object
     */
    public ReactiveLogoutController(ReactiveLogoutService service) {
        this.service = service;
    }

    @RequestMapping("/logout")
    public Mono<Object> def(ServerWebExchange exchange) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable()) {
            return service.execute(exchange);
        } else {
            return Mono.error(new ServiceNotEnableException(
                    this.getClass(),
                    "fun Mono<Object> def(ServerWebExchange exchange, RegisterContext.Request request)",
                    "logout is not enabled"
            ));
        }
    }

}
