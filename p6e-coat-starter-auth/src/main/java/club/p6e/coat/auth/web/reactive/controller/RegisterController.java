package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.RegisterService;
import club.p6e.coat.common.utils.SpringUtil;
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
@RestController
@ConditionalOnMissingBean(
        value = RegisterController.class,
        ignored = RegisterController.class
)
public class RegisterController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Password Signature Context Request Object
     * @return Password Signature Context Request Object
     */
    private Mono<RegisterContext.Request> validate(
            ServerWebExchange exchange, RegisterContext.Request request) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<RegisterContext.Request> validate(" +
                                "ServerWebExchange exchange, RegisterContext.Request request).",
                        "request parameter validation exception."
                )));
    }

    @PostMapping("/register")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody RegisterContext.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(RegisterService.class).execute(exchange, r));
    }

}
