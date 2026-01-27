package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.service.ReactiveIndexService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Reactive Index Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveIndexController.class)
@RestController("club.p6e.coat.auth.controller.ReactiveIndexController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveIndexController {

    /**
     * Reactive Index Service Object
     */
    private final ReactiveIndexService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Index Service Object
     */
    public ReactiveIndexController(ReactiveIndexService service) {
        this.service = service;
    }

    @RequestMapping("")
    public Mono<Void> def1(ServerWebExchange exchange) {
        return def(exchange);
    }

    @RequestMapping("/")
    public Mono<Void> def2(ServerWebExchange exchange) {
        return def(exchange);
    }

    @RequestMapping("/index")
    public Mono<Void> def3(ServerWebExchange exchange) {
        return def(exchange);
    }

    public Mono<Void> def(ServerWebExchange exchange) {
        return service
                .execute(exchange)
                .flatMap(r -> {
                    exchange.getResponse().getHeaders().setContentType(MediaType.valueOf(r.getType()));
                    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(r.getContent().getBytes(StandardCharsets.UTF_8))));
                });
    }

}
