package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.web.reactive.service.IndexService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Account Password Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(IndexController.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class IndexController {

    /**
     * Forgot Password Service Object
     */
    private final IndexService service;

    /**
     * Constructor Initialization
     *
     * @param service Forgot Password Service Object
     */
    public IndexController(IndexService service) {
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
                    if (r.length > 1) {
                        exchange.getResponse().getHeaders().setContentType(MediaType.valueOf(r[0]));
                        return exchange.getResponse().writeWith(
                                Mono.just(exchange.getResponse().bufferFactory()
                                        .wrap(r[1].getBytes(StandardCharsets.UTF_8))));
                    } else {
                        return Mono.empty();
                    }
                });
    }

}
