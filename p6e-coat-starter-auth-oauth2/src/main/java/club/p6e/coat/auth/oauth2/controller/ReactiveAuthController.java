package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.service.ReactiveAuthorizeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Reactive Auth Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveAuthController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.ReactiveAuthController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveAuthController {

    /**
     * Reactive Authorize Service Object
     */
    private final ReactiveAuthorizeService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Authorize Service Object
     */
    public ReactiveAuthController(ReactiveAuthorizeService service) {
        this.service = service;
    }

    @PostMapping("/oauth2/authorize")
    public Mono<Void> def(ServerWebExchange exchange, @RequestBody AuthorizeContext.Request request) {
        return service.execute(exchange, request).flatMap(r -> {
            final ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.parseMediaType(r.getType()));
            return response.writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(
                    r.getContent().getBytes(StandardCharsets.UTF_8)
            )));
        });
    }

}
