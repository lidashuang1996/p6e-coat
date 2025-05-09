package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = QuickResponseCodeLoginController.class,
        ignored = QuickResponseCodeLoginController.class
)
public class QuickResponseCodeLoginController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Quick Response Code Request Object
     * @return Login Context Quick Response Code Request Object
     */
    private Mono<LoginContext.QuickResponseCode.Request> validate(
            ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof LoginContext.QuickResponseCode.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<LoginContext.QuickResponseCode.Request> validate(" +
                                        "ServerWebExchange exchange, LoginContext.QuickResponseCode.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @PostMapping("/login/quick/response/code")
    public Mono<Object> def(ServerWebExchange exchange, @RequestBody LoginContext.QuickResponseCode.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(QuickResponseCodeLoginService.class).execute(exchange, r));
    }

}
