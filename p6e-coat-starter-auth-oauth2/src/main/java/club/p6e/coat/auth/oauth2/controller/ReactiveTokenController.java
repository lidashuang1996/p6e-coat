package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.TokenContext;
import club.p6e.coat.auth.oauth2.service.ReactiveTokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Reactive Token Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveTokenController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.ReactiveTokenController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveTokenController {

    /**
     * Reactive Token Service Object
     */
    private final ReactiveTokenService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Token Service Object
     */
    public ReactiveTokenController(ReactiveTokenService service) {
        this.service = service;
    }

    @PostMapping("/oauth2/token")
    public Mono<Map<String, Object>> def(ServerWebExchange exchange, @RequestBody TokenContext.Request request) {
        return service.execute(exchange, request);
    }

}
